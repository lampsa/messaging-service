package fi.techappeal.messagingservice.sqs;

import fi.techappeal.messagingservice.exceptions.MessagingException;
import fi.techappeal.messagingservice.exceptions.NoSuchQueueException;
import fi.techappeal.messagingservice.exceptions.RateLimitException;
import fi.techappeal.messagingservice.exceptions.ApiTimeoutException;
import software.amazon.awssdk.core.exception.ApiCallAttemptTimeoutException;
import software.amazon.awssdk.core.exception.ApiCallTimeoutException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.model.OverLimitException;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.SqsException;

/**
 * Handles SQS exceptions.
 */
public class SqsExceptionMapper {
    /**
     * Maps SQS exceptions to cloud-agnostic exceptions.
     * @param e SQS exception
     * @throws RateLimitException if the exception is an {@link OverLimitException}
     * @throws NoSuchQueueException if the exception is a {@link QueueDoesNotExistException}
     */
    public static void mapToCloudAgnosticException(SqsException e) {
        if (e instanceof OverLimitException) {
            throw new RateLimitException(e.getMessage(), e);
        } else if (e instanceof QueueDoesNotExistException) {
            throw new NoSuchQueueException(e.getMessage(), e);
        } else {
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * Maps SQS SDK exceptions to cloud-agnostic exceptions.
     * @param e SQS SDK exception
     * @throws ApiTimeoutException if the exception is an {@link ApiCallAttemptTimeoutException} or an {@link ApiCallTimeoutException}
     */
    public static void mapToCloudAgnosticException(SdkException e) {
        if(e instanceof ApiCallAttemptTimeoutException || e instanceof ApiCallTimeoutException) {
            throw new ApiTimeoutException(e.getMessage(), e);
        } else {
            throw new MessagingException(e.getMessage(), e);
        }
    }
}
