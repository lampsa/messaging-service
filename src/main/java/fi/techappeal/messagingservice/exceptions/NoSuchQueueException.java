package fi.techappeal.messagingservice.exceptions;

public class NoSuchQueueException extends MessagingException {
    public NoSuchQueueException(RuntimeException e) {
        super(e);
    }

    public NoSuchQueueException(String message, Throwable e) {
        super(message, e);
    }
}
