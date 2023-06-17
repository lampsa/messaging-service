package fi.techappeal.messagingservice;

/**
 * The processing state of a message.
 */
public enum ProcessingState {
    /**
     * The message was processed successfully by the MessageHandler.
     */
    PROCESSED,
    /**
     * The message was skipped by the MessageHandler.
     */
    SKIPPED,
    /**
     * The message was abandoned by the MessageHandler as faulty (e.g.to be placed into a dead letter queue).
     */
    ABANDONED
}
