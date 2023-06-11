package fi.techappeal.messagingservice;

import fi.techappeal.messagingservice.sqs.SqsClientWrapper;

/**
 * Builder for {@link MessagingService}.
 */
public class MessagingServiceBuilder {
    private String service = System.getProperty("message.service", "sqs");

    /**
     * Private constructor - use {@link #builder()} instead.
     */
    private MessagingServiceBuilder() {
    }

    /**
     * Create a new MessagingServiceBuilder.
     *
     * @return MessagingServiceBuilder
     */
    public static MessagingServiceBuilder builder() {
            return new MessagingServiceBuilder();
    }

    /**
     * Build the messaging service.
     *
     * @return MessagingService suitable for the configured cloud service.
     */
    public MessagingService build() {
        if (service == null) {
            throw new IllegalStateException("message.service property not set");
        }
        return switch (service) {
            case "sqs" -> new SqsClientWrapper();
            case "pubsub" -> throw new IllegalStateException("pubsub not implemented");
            case "eventgrid" -> throw new IllegalStateException("eventgrid not implemented");
            default -> throw new IllegalStateException("Unknown message.service: " + service);
        };
    }

    /**
     * Set the messaging service to use. (for testing)
     * Use {@link #builder()} instead.
     * This parameter is configurable with ${code message.service} system property.
     *
     * @param service messaging service to use: sqs, pubsub or eventgrid.
     * @return this
     */
    protected MessagingServiceBuilder service(String service) {
        this.service = service;
        return this;
    }
}
