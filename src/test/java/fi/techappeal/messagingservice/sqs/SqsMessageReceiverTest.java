package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.ProcessingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SqsMessageReceiverTest {
    private SqsMessageReceiver receiver;
    private SqsClient mockSqsClient;

    @BeforeEach
    void setUp() {
        mockSqsClient = mock(SqsClient.class);
        receiver = new SqsMessageReceiver();
        receiver.setSqsClient(mockSqsClient);
    }

    /**
     * Test that the cloud-specific message is correctly mapped to a cloud-agnostic message.
     */
    @Test
    void receiveMessage_correctResponses() {
        String queueName = "MyQ";
        String queueUrl = "mocked";

        // Arrange
        // set up two messages with different attributes and bodies for the mocked receiveMessage.getMessages() method
        Map<String, MessageAttributeValue> attributes1 = Map.of(
                "attr1", MessageAttributeValue.builder().stringValue("value1").build()
        );

        List<Message> mockReceivedMessages = Collections.singletonList(
                Message.builder().body("message1").messageId("123").messageAttributes(attributes1).build()
        );

        ReceiveMessageResponse mockReceiveMessageResponse = ReceiveMessageResponse.builder()
                .messages(mockReceivedMessages)
                .build();
        when(mockSqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(mockReceiveMessageResponse);
        receiver.setQueueUrlCache(queueName, queueUrl);

        // Act
        receiver.subscribe("MyQ", message -> {
            // Assert
            assertEquals("message1", message.getPayload());
            assertEquals("123", message.getId());
            assertEquals("value1", message.getAttributes().get("attr1"));
            receiver.stop();
            return ProcessingState.PROCESSED;
        });
    }
}