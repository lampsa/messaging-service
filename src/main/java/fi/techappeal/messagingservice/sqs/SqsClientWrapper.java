package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.MessagingService;
import fi.techappeal.messagingservice.ReceivedMessageBuilder;
import fi.techappeal.messagingservice.ReceivedMessageWrapper;
import fi.techappeal.messagingservice.SendMessageWrapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A wrapper for SQS client that maps the cloud-agnostic MessagingService interface to SQS client.
 */
public class SqsClientWrapper implements MessagingService {
    private final Map<String, String> queueUrlCache = new HashMap<>(); // queue name -> queue url cache
    private SqsClient sqsClient;

    public SqsClientWrapper() {
        String regionString = System.getProperty("MESSAGING_SERVICE_REGION", "eu-central-1");
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
                    .messageAttributes(createMessageAttributes(message.getAttributes()))
                    .build();
            sqsClient.sendMessage(sendMessageRequest);
        } catch (SqsException e) {
            SqsExceptionMapper.mapToCloudAgnosticException(e);
        }
    }


    /**
     * Receives messages from an SQS queue.
     *
     * @param queueName   name of the queue
     * @param maxMessages maximum number of messages to receive
     * @return list of messages
     */
    @Override
    public List<ReceivedMessageWrapper> receiveMessages(String queueName, int maxMessages) {
        String queueUrl = getQueueUrlForQueue(queueName);
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .messageAttributeNames("All")
                .build();

        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
        List<Message> messages = receiveMessageResponse.messages();

        return messages.stream().map(this::createMessageWrapper).collect(Collectors.toList());
    }

    /**
     * Completes processing of a message. In the case of SQS, this means deleting the message from the queue.
     *
     * @param queueName name of the queue
     * @param handle of the message to be completed
     */
    @Override
    public void completeMessage(String queueName, String handle) {
        String queueUrl = getQueueUrlForQueue(queueName);
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(handle)
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
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

    private ReceivedMessageWrapper createMessageWrapper(Message message) {
        Map<String, String> attributes = new HashMap<>();
        for (Map.Entry<String, MessageAttributeValue> entry : message.messageAttributes().entrySet()) {
            attributes.put(entry.getKey(), entry.getValue().stringValue());
        }
        return ReceivedMessageBuilder.forPayload(message.body())
            .attributes(attributes)
            .id(message.messageId())
            .handle(message.receiptHandle())
            .build();
    }
}