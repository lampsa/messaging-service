package fi.techappeal.messagingservice;

import fi.techappeal.messagingservice.sqs.SqsMessageReceiver;
import fi.techappeal.messagingservice.sqs.SqsMessageSender;

public interface MessageReceiver {
    public void subscribe(String queueName, MessageHandler messageHandler);
    public void completeMessage(String queueName, String handle);

    public void stop();

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
        public MessageReceiver build() {
            if (service == null) {
                throw new IllegalStateException("MESSAGING_SERVICE_PROVIDER property not set");
            }
            return switch (service) {
                case "sqs" -> new SqsMessageReceiver();
                case "pubsub" -> throw new IllegalStateException("pubsub not implemented");
                case "eventgrid" -> throw new IllegalStateException("eventgrid not implemented");
                default -> throw new IllegalStateException("Unknown message.service: " + service);
            };
        }
    }
}
