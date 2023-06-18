package fi.techappeal.messagingservice.exceptions;

public class ApiTimeoutException extends MessagingException {
    public ApiTimeoutException(RuntimeException e) {
        super(e);
    }

    public ApiTimeoutException(String message, Throwable e) {
        super(message, e);
    }
}
