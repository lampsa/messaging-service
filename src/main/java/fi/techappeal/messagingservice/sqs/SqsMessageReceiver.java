package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.MessageHandler;
import fi.techappeal.messagingservice.MessageReceiver;
import fi.techappeal.messagingservice.ReceivedMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQS specific implementation of {@link MessageReceiver}.
 */
public class SqsMessageReceiver implements MessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(SqsMessageReceiver.class);
    private SqsClient sqsClient;
    private final Map<String, String> queueUrlCache = new HashMap<>(); // queue name -> queue url cache
    private boolean isRunning;
    public SqsMessageReceiver() {
        String regionString = System.getProperty("MESSAGING_SERVICE_REGION", "eu-central-1");
        logger.debug("Initiating SQS receiver client using region {}", regionString);
        this.sqsClient = SqsClient.builder()
                .region(Region.of(regionString))
                .build();
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
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .build();
            logger.debug("Calling SQS receive message API");
            ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
            logger.debug("Received {} messages from queue {}", receiveMessageResponse.messages().size(), queueName);
            List<Message> messages = receiveMessageResponse.messages();
            for (Message message : messages) {
                logger.debug("Received message: {}", message.toString());
                messageHandler.onMessageReceived(createMessageWrapper(message));
                completeMessage(queueName, message.receiptHandle());
            }
        }
    }

    /**
     * Allows manually setting the SQS client. (Used for testing.)
     *
     * @param sqsClient Mocked SQS client
     */
    protected void setSqsClient(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    /**
     * Stop receiving messages from SQS queue.
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Set queue url for a queue name. (Used for testing.)
     *
     * @param queueName queue name
     * @param queueUrl  mock queue url
     */
    protected void setQueueUrlCache(String queueName, String queueUrl) {
        queueUrlCache.put(queueName, queueUrl);
    }


    /**
     * Get queue url for a queue name from the SQS service. Cache the queue url for future use.
     *
     * @param queueName queue name
     * @return queue url
     */
    private String getQueueUrlForQueue(String queueName) {
        return queueUrlCache.computeIfAbsent(queueName,
                name -> sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(name).build()).queueUrl()
        );
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
        sqsClient.deleteMessage(deleteMessageRequest);
    }
}
