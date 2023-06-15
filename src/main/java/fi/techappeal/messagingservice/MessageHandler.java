package fi.techappeal.messagingservice;

public interface MessageHandler {
    void onMessageReceived(ReceivedMessageWrapper message);
}
