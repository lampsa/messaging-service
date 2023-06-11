package fi.techappeal.messagingservice.exceptions;

public class MessagingException extends RuntimeException{
    public MessagingException(RuntimeException e) {
        super(e);
    }

    public MessagingException(String message, Throwable e) {
        super(message, e);
    }
}
