package fi.techappeal.messagingservice;

import fi.techappeal.messagingservice.sqs.SqsMessageSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ${@link MessageSender}.
 */
class MessagingServiceBuilderTest {
    /**
     * Test that MessagingServiceBuilder returns a SqsClientWrapper when the service is sqs.
     */
    @Test
    void testSqsProvider() {
        // Act
        MessageSender messagingService = new MessageSender.Builder().service("sqs").build();
        // Assert
        assertTrue(messagingService instanceof SqsMessageSender);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is pubsub.
     */
    @Test
    void testPubsubProvider() {
        // Act
        MessageSender.Builder builder = new MessageSender.Builder().service("pubsub");
        // Assert
        assertThrows(IllegalStateException.class, builder::build);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is eventgrid.
     */
    @Test
    void testEventgridProvider() {
        // Act
        MessageSender.Builder builder = new MessageSender.Builder().service("eventgrid");
        // Assert
        assertThrows(IllegalStateException.class, builder::build);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is unknown.
     */
    @Test
    void testUnknownProvider() {
        // Act
        MessageSender.Builder builder = new MessageSender.Builder().service("unknown");
        // Assert
        assertThrows(IllegalStateException.class, builder::build);
    }
}