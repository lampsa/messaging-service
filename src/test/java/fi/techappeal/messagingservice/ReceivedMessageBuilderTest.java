package fi.techappeal.messagingservice;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReceivedMessageBuilderTest {

    @Test
    void forPayload() {
        // Arrange
        ReceivedMessageBuilder builder = ReceivedMessageBuilder.forPayload("payload");
        // Act & Assert
        assertEquals("payload", builder.build().getPayload());
    }

    @Test
    void id() {
        // Arrange
        ReceivedMessageBuilder builder = ReceivedMessageBuilder.forPayload("payload")
                .id("id");
        // Act & Assert
        assertEquals("id", builder.build().getId());
    }

    @Test
    void attribute() {
        // Arrange
        ReceivedMessageBuilder builder = ReceivedMessageBuilder.forPayload("payload")
                .attribute("key", "value")
                .attribute("key2", "value2");
        // Act & Assert
        assertEquals("value", builder.build().getAttributes().get("key"));
        assertEquals("value2", builder.build().getAttributes().get("key2"));
    }

    @Test
    void attributes() {
        // Arrange
        ReceivedMessageBuilder builder = ReceivedMessageBuilder.forPayload("payload")
                .attributes(Map.of("key", "value", "key2", "value2"));
        // Act
        ReceivedMessageWrapper wrapper = builder.build();
        // Assert
        assertEquals("value", wrapper.getAttributes().get("key"));
        assertEquals("value2", wrapper.getAttributes().get("key2"));
    }

    @Test
    void handle() {
        // Arrange
        ReceivedMessageBuilder builder = ReceivedMessageBuilder.forPayload("payload")
                .handle("handle");
        // Act & Assert
        assertEquals("handle", builder.build().getHandle());
    }
}