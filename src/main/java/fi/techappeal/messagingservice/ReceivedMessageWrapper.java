package fi.techappeal.sqsproto;

import java.util.Map;

/**
 * Wraps a message that is received from messaging service in a cloud-agnostic way.
 */
public class ReceivedMessageWrapper {
    private final String id;
    private final String handle;
    private final String payload;
    private final Map<String, String> attributes;

    ReceivedMessageWrapper(String id, String handle, String payload, Map<String, String> attributes) {
        this.id = id;
        this.handle = handle;
        this.payload = payload;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }
    public String getHandle() {
        return handle;
    }

    public String getPayload() {
        return payload;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }



}

