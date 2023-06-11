package fi.techappeal.sqsproto;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for {@link ReceivedMessageWrapper}.
 */
public class ReceivedMessageBuilder {
    private String id;

    private String handle;
    private String payload;
    private final Map<String, String> attributes;

    private ReceivedMessageBuilder() {
        this.attributes = new HashMap<>();
    }

    public static ReceivedMessageBuilder forPayload(String payload) {
        ReceivedMessageBuilder builder = new ReceivedMessageBuilder();
        builder.payload = payload;
        return builder;
    }
    public ReceivedMessageBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ReceivedMessageBuilder attribute(String key, String value) {
        this.attributes.put(key, value);
        return this;
    }

    public ReceivedMessageBuilder attributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
        return this;
    }

    public ReceivedMessageBuilder handle(String handle) {
        this.handle = handle;
        return this;
    }

    public ReceivedMessageWrapper build() {
        return new ReceivedMessageWrapper(id, handle, payload, attributes);
    }
}
