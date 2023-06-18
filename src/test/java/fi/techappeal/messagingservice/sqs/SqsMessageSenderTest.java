package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.SendMessageWrapper;
import fi.techappeal.messagingservice.SqsMessagingIT;
import fi.techappeal.messagingservice.exceptions.NoSuchQueueException;
import fi.techappeal.messagingservice.exceptions.RateLimitException;
import fi.techappeal.messagingservice.exceptions.ApiTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.exception.ApiCallTimeoutException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SqsMessageSender}.
 * These tests test the behavior of the class in isolation, i.e. the actual SqsClient is mocked.
 *
 * @see SqsMessagingIT for integration tests.
 */
class SqsMessageSenderTest {
    private SqsMessageSender sender;
    private SqsClient mockSqsClient;

    @BeforeEach
    void setUp() {
        mockSqsClient = mock(SqsClient.class);
        sender = new SqsMessageSender();
        sender.setSqsClient(mockSqsClient);
    }

    /**
     * Test that the cloud-agnostic message is correctly mapped to a SQS specific request.
     */
    @Test
    void sendMessage_mapsToCorrectRequest() {
        // Arrange
        String payload = "Hello, world!";

        SendMessageWrapper message = new SendMessageWrapper.Builder()
                .payload(payload)
                .partitionKey("key")
                .attribute("attr1", "value1")
                .attribute("attr2", "value2")
                .build();
        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(null);
        sender.setQueueUrlCache("MyQ", "mocked"); // Set queue URL to avoid mocking SqsClient.getQueueUrl

        // Act
        sender.sendMessage("MyQ", message);

        // Assert
        verify(mockSqsClient).sendMessage(captor.capture());
        SendMessageRequest request = captor.getValue();
        assertEquals(payload, request.messageBody());
        assertEquals(2, request.messageAttributes().size());
        assertEquals("value1", request.messageAttributes().get("attr1").stringValue());
        assertEquals("value2", request.messageAttributes().get("attr2").stringValue());
        assertEquals("key", request.messageGroupId());
    }

    /**
     * Test that SQS specific OverLimitException is mapped to a generic RateLimitException.
     */
    @Test
    void sendMessage_RateExceeded() {
        // Arrange
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(OverLimitException.builder().message("Over limit").build());
        SendMessageWrapper message = new SendMessageWrapper.Builder().payload("Hello, world!").build();
        String queueName = "MyQ";
        sender.setQueueUrlCache(queueName, "ignore"); // Set queue URL to avoid mocking SqsClient.getQueueUrl

        // Act and Assert
        assertThrows(RateLimitException.class, () -> sender.sendMessage(queueName, message));
    }

    /**
     * Test that SQS specific QueueDoesNotExistException is mapped to a generic NoSuchQueueException.
     */
    @Test
    void sendMessage_NoSuchQueue() {
        // Arrange
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(QueueDoesNotExistException.builder().message("Queue does not exist").build());
        SendMessageWrapper message = new SendMessageWrapper.Builder().payload("Hello, world!").build();
        String queueName = "MyQ";
        sender.setQueueUrlCache(queueName, "ignore"); // Set queue URL to avoid mocking SqsClient.getQueueUrl

        // Act and Assert
        assertThrows(NoSuchQueueException.class, () -> sender.sendMessage(queueName, message));
    }

    /**
     * Test that SQS specific SdkClientException is mapped to a generic ApimTimeoutException.
     */
    @Test
    void sendMessage_Timeout() {
        // Arrange
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(ApiCallTimeoutException.builder().message("Timeout").build());
        SendMessageWrapper message = new SendMessageWrapper.Builder().payload("Hello, world!").build();
        String queueName = "MyQ";
        sender.setQueueUrlCache(queueName, "ignore"); // Set queue URL to avoid mocking SqsClient.getQueueUrl

        // Act and Assert
        assertThrows(ApiTimeoutException.class, () -> sender.sendMessage(queueName, message));
    }
}