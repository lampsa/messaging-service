package fi.techappeal.messagingservice;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a message that is received from messaging service in a cloud-agnostic way.
 */
public class ReceivedMessageWrapper {
    private final String id;
    private final String payload;
    private final Map<String, String> attributes;

    private ReceivedMessageWrapper(String id, String payload, Map<String, String> attributes) {
        this.id = id;
        this.payload = payload;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public static class Builder {
        private String id;

        private String payload;
        private Map<String, String> attributes;

        public Builder() {
            this.attributes = new HashMap<>();
        }

        public Builder payload(String payload) {
            Builder builder = new Builder();
            builder.payload = payload;
            return builder;
        }
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder attribute(String key, String value) {
            this.attributes.put(key, value);
            return this;
        }

        public Builder attributes(Map<String, String> attributes) {
            this.attributes.putAll(attributes);
            return this;
        }

        public ReceivedMessageWrapper build() {
            return new ReceivedMessageWrapper(id, payload, attributes);
        }
    }

}

