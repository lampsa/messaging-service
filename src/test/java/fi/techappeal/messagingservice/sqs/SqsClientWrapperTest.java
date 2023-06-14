package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.ReceivedMessageWrapper;
import fi.techappeal.messagingservice.SendMessageBuilder;
import fi.techappeal.messagingservice.SendMessageWrapper;
import fi.techappeal.messagingservice.SqsMessagingIT;
import fi.techappeal.messagingservice.exceptions.NoSuchQueueException;
import fi.techappeal.messagingservice.exceptions.RateLimitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SqsClientWrapper}.
 * These tests test the behavior of the class in isolation, i.e. the actual SqsClient is mocked.
 *
 * @see SqsMessagingIT for integration tests.
 */
class SqsClientWrapperTest {
    private SqsClientWrapper clientWrapper;
    private SqsClient mockSqsClient;

    @BeforeEach
    void setUp() {
        mockSqsClient = mock(SqsClient.class);
        clientWrapper = new SqsClientWrapper();
        clientWrapper.setSqsClient(mockSqsClient);
    }

    /**
     * Test that the cloud-agnostic message is correctly mapped to a SQS specific request.
     */
    @Test
    void sendMessage_mapsToCorrectRequest() {
        // Arrange
        String payload = "Hello, world!";
        SendMessageWrapper message = SendMessageBuilder.forPayload(payload)
                .partitionKey("key")
                .attribute("attr1", "value1")
                .attribute("attr2", "value2")
                .build();
        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(null);
        clientWrapper.setQueueUrlCache("MyQ", "mocked"); // Set queue URL to avoid mocking SqsClient.getQueueUrl

        // Act
        clientWrapper.sendMessage("MyQ", message);

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
        SendMessageWrapper message = SendMessageBuilder.forPayload("Hello, world!").build();
        String queueName = "MyQ";
        clientWrapper.setQueueUrlCache(queueName, "ignore"); // Set queue URL to avoid mocking SqsClient.getQueueUrl

        // Act and Assert
        assertThrows(RateLimitException.class, () -> clientWrapper.sendMessage(queueName, message));
    }

    /**
     * Test that SQS specific QueueDoesNotExistException is mapped to a generic NoSuchQueueException.
     */
    @Test
    void sendMessage_NoSuchQueue() {
        // Arrange
        when(mockSqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenThrow(QueueDoesNotExistException.builder().message("Queue does not exist").build());
        SendMessageWrapper message = SendMessageBuilder.forPayload("Hello, world!").build();
        String queueName = "MyQ";
        clientWrapper.setQueueUrlCache(queueName, "ignore"); // Set queue URL to avoid mocking SqsClient.getQueueUrl

        // Act and Assert
        assertThrows(NoSuchQueueException.class, () -> clientWrapper.sendMessage(queueName, message));
    }

    /**
     * Test that received messages from the queue are correctly mapped to a cloud-agnostic messages.
     * The receiveMessage.getMessages() method is mocked to return a list of two messages.
     */
    @Test
    void receiveMessages_correctResponses() {
        int maxMessages = 10;
        String queueName = "MyQ";
        String queueUrl = "mocked";

        // Arrange
        // set up two messages with different attributes and bodies for the mocked receiveMessage.getMessages() method
        Map<String, MessageAttributeValue> attributes1 = Map.of(
                "attr1", MessageAttributeValue.builder().stringValue("value1").build()
        );
        Map<String, MessageAttributeValue> attributes2 = Map.of(
                "attr2", MessageAttributeValue.builder().stringValue("value2").build()
        );
        List<Message> mockReceivedMessages = Arrays.asList(
                Message.builder().body("message1").messageId("123").messageAttributes(attributes1).build(),
                Message.builder().body("message2").messageId("124").messageAttributes(attributes2).build()
        );

        ReceiveMessageResponse mockReceiveMessageResponse = ReceiveMessageResponse.builder()
                .messages(mockReceivedMessages)
                .build();
        when(mockSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(mockReceiveMessageResponse);
        clientWrapper.setQueueUrlCache(queueName, queueUrl);

        // Act
        List<ReceivedMessageWrapper> result = clientWrapper.receiveMessages(queueName, maxMessages);

        // Assert
        assertEquals(mockReceivedMessages.size(), result.size());
        for(int i = 0; i < mockReceivedMessages.size(); i++) {
            assertEquals(mockReceivedMessages.get(i).body(), result.get(i).getPayload());
            assertEquals(mockReceivedMessages.get(i).messageId(), result.get(i).getId());
            assertEquals(mockReceivedMessages.get(i).messageAttributes().size(), result.get(i).getAttributes().size());
        }
        assertEquals("value1", result.get(0).getAttributes().get("attr1"));
        assertEquals("value2", result.get(1).getAttributes().get("attr2"));
    }
}