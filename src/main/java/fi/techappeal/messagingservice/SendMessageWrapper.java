package fi.techappeal.messagingservice;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a message that is sent to messaging service in a cloud-agnostic way.
 */
public class SendMessageWrapper {
    private final String payload;
    private final String partitionKey;
    private final Map<String, String> attributes;

    private SendMessageWrapper(String payload, String partitionKey, Map<String, String> attributes) {
        this.payload = payload;
        this.partitionKey = partitionKey;
        this.attributes = attributes;
    }

    /**
     * Returns the attributes of the message.
     * @return
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Returns the payload of the message.
     * @return payload of the message
     */
    public String getPayload() {
        return payload;
    }


    public String getPartitionKey() {
        return partitionKey;
    }

    public static class Builder {
        private String payload;
        private String partitionKey;
        private Map<String, String> attributes;

        public Builder() {
            this.attributes = new HashMap<>();
        }

        public Builder payload(String payload) {
            Builder builder = new Builder();
            builder.payload = payload;
            return builder;
        }

        public Builder attribute(String key, String value) {
            this.attributes.put(key, value);
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes.putAll(attributes);
            return this;
        }

        public Builder partitionKey(String partitionKey) {
            this.partitionKey = partitionKey;
            return this;
        }

        public SendMessageWrapper build() {
            return new SendMessageWrapper(payload, partitionKey, attributes);
        }
    }
}
