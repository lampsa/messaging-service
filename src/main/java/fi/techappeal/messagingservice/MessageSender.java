package fi.techappeal.messagingservice;
import java.util.List;

/**
 * Cloud-agostic messaging service interface.
 */
public interface MessageSender {
    /**
     * Send a message to a specified queue.
     * @param queueName name of the queue
     * @param message message to be sent
     */
    void sendMessage(String queueName, SendMessageWrapper message);

    /**
     * Close the messaging service.
     */
    void close();
    static class Builder {
        private String service = System.getProperty("MESSAGING_SERVICE_PROVIDER", "sqs");

        /**
         * Set the messaging service provider. (Used for testing.)
         * @param service messaging service provider
         * @return Builder
         */
        protected Builder service(String service) {
            this.service = service;
            return this;
        }

        /**
         * Create a cloud-specific MessageSender instance.
         * @return MessageSender
         */
        public MessageSender build() {
            if (service == null) {
                throw new IllegalStateException("MESSAGING_SERVICE_PROVIDER property not set");
            }
            return switch (service) {
                case "sqs" -> new fi.techappeal.messagingservice.sqs.SqsMessageSender();
                case "pubsub" -> throw new IllegalStateException("pubsub not implemented");
                case "eventgrid" -> throw new IllegalStateException("eventgrid not implemented");
                default -> throw new IllegalStateException("Unknown message.service: " + service);
            };
        }
    }
}