package fi.techappeal.messagingservice;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for creating a message that is sent to messaging service in a cloud-agnostic way.
 */
public class SendMessageBuilder {
    private String payload;
    private String partitionKey;
    private final Map<String, String> attributes;

    private SendMessageBuilder() {
        this.attributes = new HashMap<>();
    }

    /**
     * Creates a builder for a message with the specified payload.
     * @param payload payload of the message
     * @return this builder
     */
    public static SendMessageBuilder forPayload(String payload) {
        SendMessageBuilder builder = new SendMessageBuilder();
        builder.payload = payload;
        return builder;
    }

    /**
     * Adds attributes to the message.
     * @param attributes attributes to add
     * @return this builder
     */
    public SendMessageBuilder attributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
        return this;
    }

    /**
     * Adds an attribute to the message.
     * @param key key of the attribute
     * @param value value of the attribute
     * @return this builder
     */
    public SendMessageBuilder attribute(String key, String value) {
        this.attributes.put(key, value);
        return this;
    }

    /**
     * Sets the partition key of the message.
     * @param partitionKey partition key of the message
     * @return this builder
     */
    public SendMessageBuilder partitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
        return this;
    }

    public SendMessageWrapper build() {
        return new SendMessageWrapper(payload, partitionKey, attributes);
    }
}
