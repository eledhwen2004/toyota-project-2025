package com.main.ClientConnection;

/**
 * Interface defining the contract for handling client connections.
 * <p>
 * This interface outlines essential operations such as processing client messages,
 * subscribing/unsubscribing to specific rates, and periodically sending subscribed rate data.
 */
public interface ClientConnectionInterface {

    /**
     * Handles an incoming message from the client and triggers the appropriate action.
     *
     * @param message the message received from the client
     */
    void handleMessageTaken(String message);

    /**
     * Subscribes the client to a specified rate.
     *
     * @param rateName the name of the rate to subscribe to
     */
    void subscribeToRate(String rateName);

    /**
     * Unsubscribes the client from a previously subscribed rate.
     *
     * @param rateName the name of the rate to unsubscribe from
     */
    void unsubscribeFromRate(String rateName);

    /**
     * Sends updated rate information for all subscribed rates to the client periodically.
     *
     * @throws InterruptedException if the sending thread is interrupted
     */
    void sendSubscribedRates() throws InterruptedException;
}
