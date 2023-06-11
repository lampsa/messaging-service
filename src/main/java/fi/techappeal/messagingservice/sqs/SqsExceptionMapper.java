package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.exceptions.MessagingException;
import fi.techappeal.messagingservice.exceptions.RateLimitException;
import software.amazon.awssdk.services.sqs.model.OverLimitException;
import software.amazon.awssdk.services.sqs.model.SqsException;

/**
 * Handles SQS exceptions.
 */
public class SqsExceptionMapper {
    /**
     * Maps SQS exceptions to cloud-agnostic exceptions.
     * @param e SQS exception
     * @throws RateLimitException if the exception is an {@link OverLimitException}
     */
    public static void mapToCloudAgnosticException(SqsException e) {
        if (e instanceof OverLimitException) {
            throw new RateLimitException(e.getMessage(), e);
        } else {
            System.out.println("SQS exception: " + e.getMessage());
            throw new MessagingException(e.getMessage(), e);
        }
    }
}
