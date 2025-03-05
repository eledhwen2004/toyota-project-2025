package com.main.ClientConnection;

public interface ClientConnectionInterface {
    void handleMessageTaken(String message);
    void subscribeToRate(String rateName);
    void unsubscribeFromRate(String rateName);
    void sendSubscribedRates() throws InterruptedException;
}
