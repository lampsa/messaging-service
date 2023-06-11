package fi.techappeal.messagingservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ${@link MessagingServiceBuilder}.
 */
class MessagingServiceBuilderTest {
    /**
     * Test that MessagingServiceBuilder returns a SqsClientWrapper when the service is sqs.
     */
    @Test
    void testSqsProvider() {
        // Act
        MessagingService messagingService = MessagingServiceBuilder.builder()
                .service("sqs")
                .build();
        // Assert
        assertTrue(messagingService instanceof fi.techappeal.messagingservice.sqs.SqsClientWrapper);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is pubsub.
     */
    @Test
    void testPubsubProvider() {
        // Act
        MessagingServiceBuilder builder = MessagingServiceBuilder.builder()
                .service("pubsub");
        // Assert
        assertThrows(IllegalStateException.class, builder::build);
    }

    /**
     * Test that the MessagingServiceBuilder throws an exception when the service is eventgrid.
     */
    @Test
    void testEventgridProvider() {
        // Act
        MessagingServiceBuilder builder = MessagingServiceBuilder.builder()
                .service("eventgrid");
        // Assert
        assertThrows(IllegalStateException.class, builder::build);
    }
}