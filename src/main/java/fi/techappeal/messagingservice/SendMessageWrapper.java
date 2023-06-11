package fi.techappeal.sqsproto;

import java.util.Map;

public class SendMessageWrapper {
    private final String payload;
    private final Map<String, String> attributes;

    SendMessageWrapper(String payload, Map<String, String> attributes) {
        this.payload = payload;
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getPayload() {
        return payload;
    }
}
