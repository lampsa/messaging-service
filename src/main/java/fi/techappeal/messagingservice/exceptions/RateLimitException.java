package fi.techappeal.messagingservice.exceptions;

/**
 * Thrown when a rate limit is exceeded.
 */
public class RateLimitException extends MessagingException {
    public RateLimitException(RuntimeException e) {
        super(e);
    }
    public RateLimitException(String message, Throwable e) {
        super(message, e);
    }
}
