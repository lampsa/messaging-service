package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for SQS client that maps the cloud-agnostic MessagingService interface to SQS client.
 */
public class SqsMessageSender implements MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(SqsMessageSender.class);
    private final Map<String, String> queueUrlCache = new HashMap<>(); // queue name -> queue url cache
    private SqsClient sqsClient;

    public SqsMessageSender() {
        String regionString = System.getProperty("MESSAGING_SERVICE_REGION", "eu-central-1");
        logger.debug("Initiating SQS sender client using region {}", regionString);
        this.sqsClient = SqsClient.builder()
            .region(Region.of(regionString))
            .build();
    }

    /**
     * Sends a message to an SQS queue.
     *
     * @param queueName name of the queue
     * @param message   cloud-agnostic message
     */
    @Override
    public void sendMessage(String queueName, SendMessageWrapper message) {
        try {
            String queueUrl = getQueueUrlForQueue(queueName);
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message.getPayload())
                .messageGroupId(message.getPartitionKey())
                .messageAttributes(createMessageAttributes(message.getAttributes()))
                .build();
            logger.info("Sending message [{}] to queue {}", sendMessageRequest.toString(), queueName);
            SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);
        } catch (SqsException e) {
            SqsExceptionMapper.mapToCloudAgnosticException(e);
        }
    }

    @Override
    public void close() {
        sqsClient.close();
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
     * Set queue url for a queue name. (Used for testing.)
     *
     * @param queueName queue name
     * @param queueUrl  mock queue url
     */
    protected void setQueueUrlCache(String queueName, String queueUrl) {
        queueUrlCache.put(queueName, queueUrl);
    }

    /**
     * Create SQS message attributes from a map of cloud-agnostic attributes.
     * String is the only supported data type as it is the least common denominator between SQS
     * and other messaging services.
     *
     * @param attributes as a map of key-value pairs
     * @return SQS message attributes
     */
    private Map<String, MessageAttributeValue> createMessageAttributes(Map<String, String> attributes) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        for (String key : attributes.keySet()) {
            MessageAttributeValue messageAttributeValue = MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(attributes.get(key))
                .build();
            messageAttributes.put(key, messageAttributeValue);
        }
        return messageAttributes;
    }
    private String getQueueUrlForQueue(String queueName) {
        return queueUrlCache.computeIfAbsent(queueName,
                name -> sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(name).build()).queueUrl()
        );
    }
}