package fi.techappeal.messagingservice;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test for SQS messaging service.
 * Assumes that AWS credentials are available.
 * Assumes that there is a queue named "MyQ" in AWS account.
 * Assumes that the queue is empty.
 */
public class SqsMessagingIT {
    /**
     * Test that we can send, receive and complete a message.
     */
    @Test
    void testSendReceiveCompleteMessage() {
        // Start listener thread
        Thread listenerThread = new Thread(() -> {
            MessageReceiver queueReceiver = new MessageReceiver.Builder().service("sqs").build();
            queueReceiver.subscribe("MyQ", (ReceivedMessageWrapper receivedMessage) -> {
                System.out.println(receivedMessage.toString());
                assertEquals("Hello World", receivedMessage.getPayload());
                assertEquals("value1", receivedMessage.getAttributes().get("attr1"));
                assertEquals("value2", receivedMessage.getAttributes().get("attr2"));
                queueReceiver.stop();
            });
        });
        listenerThread.start();

        // Wait for a brief moment to allow the listener thread to start
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Arrange
        MessageSender queueSender = new MessageSender.Builder().service("sqs").build();
        SendMessageWrapper message = SendMessageBuilder
            .forPayload("Hello World")
            .attribute("attr1", "value1")
            .attribute("attr2", "value2")
            .build();

        // Act
        System.out.println("Sending message: " + message.toString());
        queueSender.sendMessage("MyQ", message);

        // Wait for the listener thread to complete
        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
