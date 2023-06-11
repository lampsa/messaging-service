package fi.techappeal.sqsproto;
import java.util.List;

/**
 * Cloud-agostic messaging service interface.
 */
public interface MessagingService {
    /**
     * Send a message to a specified queue.
     * @param queueName name of the queue
     * @param message message to be sent
     */
    void sendMessage(String queueName, SendMessageWrapper message);

    /**
     * Receive messages from the messaging service.
     * @param queueName name of the queue
     * @param maxMessages maximum number of messages to receive
     * @return list of messages received
     */
    List<ReceivedMessageWrapper> receiveMessages(String queueName, int maxMessages);

    /**
     * Complete processing a message.
     * @param queueName name of the queue
     * @param messageId id of the message to be completed
     */
    void completeMessage(String queueName, String messageId);

    /**
     * Close the messaging service.
     */
    void close();
}