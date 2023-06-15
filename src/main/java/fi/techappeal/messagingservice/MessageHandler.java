package fi.techappeal.messagingservice;

/**
 * Message handler interface that should be implemented by the client.
 */
public interface MessageHandler {
    /**
     * Called when a message is received from the queue.
     * @param message the cloud-agnostic message wrapper
     */
    void onMessageReceived(ReceivedMessageWrapper message);
}
