package fi.techappeal.messagingservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for MessagingService.
 */
public class SqsMessagingIT {
    @Test
    void test() {
        // Arrange
        MessagingService queueService = MessagingServiceBuilder
            .builder()
            .service("sqs")
            .build();
        SendMessageWrapper message = SendMessageBuilder
            .forPayload("Hello World")
            .attribute("attr1", "value1")
            .attribute("attr2", "value2")
            .build();

        // Act
        queueService.sendMessage("MyQ", message);
        List<ReceivedMessageWrapper> messages = queueService.receiveMessages("MyQ", 1);

        // Assert
        assertEquals(1, messages.size());
        ReceivedMessageWrapper receivedMessage = messages.get(0);
        assertEquals("Hello World", receivedMessage.getPayload());
        assertEquals(2, receivedMessage.getAttributes().size());
        assertEquals("value1", receivedMessage.getAttributes().get("attr1"));
        assertEquals("value2", receivedMessage.getAttributes().get("attr2"));

        // Act
        queueService.completeMessage("MyQ", receivedMessage.getHandle());
    }
}
