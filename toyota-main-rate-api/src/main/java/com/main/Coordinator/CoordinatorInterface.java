package com.main.Coordinator;

import com.main.Dto.RateDto;
import com.main.Dto.RateStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Interface that defines the callback methods used by platform-specific subscribers to notify the {@link Coordinator}.
 * <p>
 * Implementing classes should handle subscriber connections, disconnections, rate updates,
 * availability notifications, and provide rate status information.
 */
@Component
public interface CoordinatorInterface {

    /**
     * Callback triggered when a subscriber connects to the platform.
     *
     * @param platformName the name of the platform (subscriber)
     * @param status       true if connection is successful, false otherwise
     * @throws IOException if an error occurs during post-connection operations
     */
    void onConnect(String platformName, Boolean status) throws IOException;

    /**
     * Callback triggered when a subscriber disconnects from the platform.
     *
     * @param platformName the name of the platform (subscriber)
     * @param status       true if disconnection was expected or clean, false if it was an error
     * @throws IOException if an error occurs during cleanup or recovery
     */
    void onDisConnect(String platformName, Boolean status) throws IOException;

    /**
     * Callback triggered when a rate becomes available for the first time.
     *
     * @param platformName the platform providing the rate
     * @param rateName     the name of the rate
     * @param rate         the rate data received
     */
    void onRateAvailable(String platformName, String rateName, RateDto rate);

    /**
     * Callback triggered when a rate that is already available gets updated.
     *
     * @param platformName the platform providing the update
     * @param rateName     the name of the rate
     * @param rate         the updated rate data
     */
    void onRateUpdate(String platformName, String rateName, RateDto rate);

    /**
     * Requests the current status of a given rate.
     *
     * @param platformName the name of the platform requesting status
     * @param rateName     the name of the rate being queried
     * @return the {@link RateStatus} representing the current availability of the rate
     */
    RateStatus onRateStatus(String platformName, String rateName);
}
