package com.main.Subscriber;

import com.main.Coordinator.CoordinatorInterface;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Base interface for all subscriber components.
 * <p>
 * This interface acts as a contract for classes that establish platform connections,
 * manage subscriptions, and handle rate data.
 * <p>
 * Implementations may include different communication mechanisms such as
 * {@link com.main.Subscriber.Subscribers.RESTSubscriber} and {@link com.main.Subscriber.Subscribers.TCPSubscriber}.
 */
@Component
public interface SubscriberInterface {

    /**
     * Connects to the platform using the provided username and password.
     *
     * @param platformName the name of the platform (e.g., PF1, PF2)
     * @param userName     the username for authentication
     * @param password     the password for authentication
     * @throws IOException if an error occurs during connection
     */
    void connect(String platformName, String userName, String password) throws IOException;

    /**
     * Disconnects from the platform.
     *
     * @param platformName the name of the platform
     * @param userName     the username used for disconnection
     * @param password     the password used for disconnection
     * @throws IOException if an error occurs while disconnecting
     */
    void disConnect(String platformName, String userName, String password) throws IOException;

    /**
     * Subscribes to the specified rate.
     *
     * @param platformName the name of the platform
     * @param rateName     the name of the rate to subscribe to (e.g., USDTRY)
     * @throws IOException if an error occurs during subscription
     */
    void subscribe(String platformName, String rateName) throws IOException;

    /**
     * Unsubscribes from a previously subscribed rate.
     *
     * @param platformName the name of the platform
     * @param rateName     the name of the rate to unsubscribe from
     * @throws IOException if an error occurs while unsubscribing
     */
    void unSubscribe(String platformName, String rateName) throws IOException;

    /**
     * Sets the coordinator for the subscriber. The coordinator manages the
     * connection, subscription, and rate data status.
     *
     * @param coordinator the {@link CoordinatorInterface} instance managing the system
     */
    void setCoordinator(CoordinatorInterface coordinator);
}
