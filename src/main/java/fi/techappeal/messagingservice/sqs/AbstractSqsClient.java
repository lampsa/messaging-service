package fi.techappeal.messagingservice.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractSqsClient {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSqsClient.class);
    private SqsClient sqsClient;
    private final Map<String, String> queueUrlCache = new HashMap<>(); // queue name -> queue url cache
    AbstractSqsClient() {
        super();
        String regionString = System.getProperty("MESSAGING_SERVICE_REGION", "eu-central-1");
        logger.debug("Initiating SQS client using region: {}", regionString);
        this.sqsClient = SqsClient.builder()
                .region(Region.of(regionString))
                .build();
    }

    /**
     * Get SQS client.
     *
     * @return SQS client
     */
    protected SqsClient getSqsClient() {
        return sqsClient;
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
     * Get queue url for a queue name from the SQS service. Cache the queue url for future use.
     *
     * @param queueName queue name
     * @return queue url
     */
    protected String getQueueUrlForQueue(String queueName) {
        return queueUrlCache.computeIfAbsent(queueName,
                name -> sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(name).build()).queueUrl()
        );
    }
}
