package fi.techappeal.messagingservice;

import fi.techappeal.messagingservice.sqs.SqsMessageReceiver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ${@link MessageReceiver}.
 */
class MessageReceiverTest {
    /**
     * Test that MessagingServiceBuilder returns a SqsClientWrapper when the service is sqs.
     */
    @Test
    void testSqsProvider() {
        MessageReceiver messagingService = new MessageReceiver.Builder().service("sqs").build();
        assertTrue(messagingService instanceof SqsMessageReceiver);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is the unimplemented pubsub.
     */
    @Test
    void testPubsubProvider() {
        MessageReceiver.Builder builder = new MessageReceiver.Builder().service("pubsub");
        assertThrows(IllegalStateException.class, builder::build);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is the unimplemented eventgrid.
     */
    @Test
    void testEventgridProvider() {
        MessageReceiver.Builder builder = new MessageReceiver.Builder().service("eventgrid");
        assertThrows(IllegalStateException.class, builder::build);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is unknown.
     */
    @Test
    void testUnknownProvider() {
        MessageReceiver.Builder builder = new MessageReceiver.Builder().service("unknown");
        assertThrows(IllegalStateException.class, builder::build);
    }

}