package fi.techappeal.messagingservice;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageBuilderTest {
    @Test
    void forPayload() {
        // Act
        SendMessageBuilder builder = SendMessageBuilder.forPayload("payload");
        // Assert
        assertEquals("payload", builder.build().getPayload());
    }

    @Test
    void attributes() {
        // Act
        SendMessageBuilder builder = SendMessageBuilder.forPayload("payload")
                .attributes(Map.of("key", "value", "key2", "value2"));
        SendMessageWrapper wrapper = builder.build();
        // Assert
        assertEquals("value", wrapper.getAttributes().get("key"));
        assertEquals("value2", wrapper.getAttributes().get("key2"));
    }

    @Test
    void attribute() {
        // Act
        SendMessageBuilder builder = SendMessageBuilder.forPayload("payload")
                .attribute("key", "value")
                .attribute("key2", "value2");
        SendMessageWrapper wrapper = builder.build();
        // Assert
        assertEquals("value", wrapper.getAttributes().get("key"));
        assertEquals("value2", wrapper.getAttributes().get("key2"));
    }
}