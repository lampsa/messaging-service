package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.MessageHandler;
import fi.techappeal.messagingservice.MessageReceiver;
import fi.techappeal.messagingservice.ProcessingState;
import fi.techappeal.messagingservice.ReceivedMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQS specific implementation of {@link MessageReceiver}.
 */
public class SqsMessageReceiver extends AbstractSqsClient implements MessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(SqsMessageReceiver.class);
    private boolean isRunning;
    private final Integer visibilityTimeout = System.getenv("SQS_VISIBILITY_TIMEOUT") != null ?
            Integer.parseInt(System.getenv("SQS_VISIBILITY_TIMEOUT")) : 20;
    private final Integer maxNumberOfMessages = System.getenv("SQS_MAX_NUMBER_OF_MESSAGES") != null ?
            Integer.parseInt(System.getenv("SQS_MAX_NUMBER_OF_MESSAGES")) : 10;

    public SqsMessageReceiver() {
       super();
    }

    /**
     * Subscribe to cloud-agnostic messages from SQS queue.
     *
     * @param queueName name of the queue
     * @param messageHandler cloud-agnostic message handler
     */
    public void subscribe(String queueName, MessageHandler messageHandler) {
        String queueUrl = getQueueUrlForQueue(queueName);
        isRunning = true;

        logger.debug("Starting to receive messages from queue: {}", queueUrl);
        while (isRunning) {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageAttributeNames("All")
                    .maxNumberOfMessages(maxNumberOfMessages)
                    .waitTimeSeconds(visibilityTimeout)
                    .build();
            logger.debug("Calling SQS receive message API");
            ReceiveMessageResponse receiveMessageResponse = getSqsClient().receiveMessage(receiveMessageRequest);
            logger.debug("Received {} messages from queue {}", receiveMessageResponse.messages().size(), queueName);
            List<Message> messages = receiveMessageResponse.messages();
            ProcessingState state;
            for (Message message : messages) {
                logger.debug("Received message: {}", message.toString());
                state = messageHandler.onMessageReceived(createMessageWrapper(message));
                switch (state) {
                    case PROCESSED: completeMessage(queueName, message.receiptHandle()); break;
                    case SKIPPED: break;
                    case ABANDONED:
                        logger.warn("Abandoning message: {}", message.messageId());
                        throw new IllegalStateException("Dead letter queue not implemented");
                }
            }
        }
    }

    /**
     * Stop receiving messages from SQS queue.
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Create a cloud-agnostic message wrapper from an SQS message.
     *
     * @param message SQS message
     * @return cloud-agnostic message wrapper
     */
    private ReceivedMessageWrapper createMessageWrapper(Message message) {
        Map<String, String> attributes = new HashMap<>();
        for (Map.Entry<String, MessageAttributeValue> entry : message.messageAttributes().entrySet()) {
            attributes.put(entry.getKey(), entry.getValue().stringValue());
        }
        return new ReceivedMessageWrapper.Builder().payload(message.body())
                .attributes(attributes)
                .id(message.messageId())
                .build();
    }

    /**
     * Completes processing of a message. In the case of SQS, this means deleting the message from the queue.
     *
     * @param queueName name of the queue
     * @param handle of the message to be completed
     */
    private void completeMessage(String queueName, String handle) {
        String queueUrl = getQueueUrlForQueue(queueName);
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(handle)
                .build();
        getSqsClient().deleteMessage(deleteMessageRequest);
    }
}
