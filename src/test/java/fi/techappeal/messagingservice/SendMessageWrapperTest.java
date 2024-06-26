package fi.techappeal.messagingservice;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageWrapperTest {
    @Test
    void forPayload() {
        // Act
        SendMessageWrapper.Builder builder = new SendMessageWrapper.Builder().payload("payload");
        // Assert
        assertEquals("payload", builder.build().getPayload());
    }

    @Test
    void attributes() {
        // Act
        SendMessageWrapper.Builder builder = new SendMessageWrapper.Builder()
                .payload("payload")
                .attributes(Map.of("key", "value", "key2", "value2"));
        SendMessageWrapper wrapper = builder.build();
        // Assert
        assertEquals("value", wrapper.getAttributes().get("key"));
        assertEquals("value2", wrapper.getAttributes().get("key2"));
    }

    @Test
    void attribute() {
        // Act
        SendMessageWrapper.Builder builder = new SendMessageWrapper.Builder()
                .payload("payload")
                .attribute("key", "value")
                .attribute("key2", "value2");
        SendMessageWrapper wrapper = builder.build();
        // Assert
        assertEquals("value", wrapper.getAttributes().get("key"));
        assertEquals("value2", wrapper.getAttributes().get("key2"));
    }
}