package fi.techappeal.sqsproto;

import java.util.HashMap;
import java.util.Map;

public class SendMessageBuilder {
    private String payload;
    private Map<String, String> attributes;

    private SendMessageBuilder() {
        this.attributes = new HashMap<>();
    }

    public static SendMessageBuilder forPayload(String payload) {
        SendMessageBuilder builder = new SendMessageBuilder();
        builder.payload = payload;
        return builder;
    }

    public SendMessageBuilder attributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
        return this;
    }

    public SendMessageBuilder attribute(String key, String value) {
        this.attributes.put(key, value);
        return this;
    }

    public SendMessageWrapper build() {
        return new SendMessageWrapper(payload, attributes);
    }
}
