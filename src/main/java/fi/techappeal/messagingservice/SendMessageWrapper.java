package fi.techappeal.messagingservice;

import java.util.Map;

/**
 * Wraps a message that is sent to messaging service in a cloud-agnostic way.
 */
public class SendMessageWrapper {
    private final String payload;
    private final Map<String, String> attributes;

    SendMessageWrapper(String payload, Map<String, String> attributes) {
        this.payload = payload;
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
}
