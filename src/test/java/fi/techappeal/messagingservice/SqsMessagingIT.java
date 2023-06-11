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
        ReceivedMessageWrapper msg = messages.get(0);
        assertEquals("Hello World", msg.getPayload());
        assertEquals(2, msg.getAttributes().size());g
    }
}
