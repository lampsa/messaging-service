package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.MessagingService;
import fi.techappeal.messagingservice.ReceivedMessageWrapper;
import fi.techappeal.messagingservice.SendMessageWrapper;

import java.util.List;

/**
 * A wrapper for SQS client that maps the cloud-agnostic MessagingService interface to SQS client.
 */
public class SqsClientWrapper implements MessagingService {
    @Override
    public void sendMessage(String queueName, SendMessageWrapper message) {

    }

    @Override
    public List<ReceivedMessageWrapper> receiveMessages(String queueName, int maxMessages) {
        return null;
    }

    @Override
    public void completeMessage(String queueName, String messageId) {

    }

    @Override
    public void close() {

    }
}
