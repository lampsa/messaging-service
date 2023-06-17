package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for SQS client that maps the cloud-agnostic MessagingService interface to SQS client.
 */
public class SqsMessageSender extends AbstractSqsClient implements MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(SqsMessageSender.class);

    public SqsMessageSender() {
        super();
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
            logger.debug("Sending message [{}] to queue {}", sendMessageRequest.toString(), queueName);
            SendMessageResponse response = getSqsClient().sendMessage(sendMessageRequest);
        } catch (SqsException e) {
            SqsExceptionMapper.mapToCloudAgnosticException(e);
        }
    }

    @Override
    public void close() {
        getSqsClient().close();
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
}