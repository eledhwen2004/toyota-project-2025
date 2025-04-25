package com.main.Subscriber.Subscribers;

import com.main.Authentication.LoginRequest;
import com.main.Authentication.UserAuth;
import com.main.Coordinator.CoordinatorInterface;
import com.main.Dto.RateDto;
import com.main.Dto.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The `RESTSubscriber` class represents a subscriber that communicates with a server to receive rate updates via RESTFUL APIs.
 * This class manages the connection to the server, subscribes to rate updates, and processes the rate data as it becomes available.
 * <p>
 * It extends the `Thread` class to allow continuous rate updates, running in a separate thread once connected.
 * It integrates with a `CoordinatorInterface` to notify the coordinator of changes to the connection or rate updates.
 * </p>
 *
 * @see SubscriberInterface
 * @see CoordinatorInterface
 * @see RateDto
 * @see RateStatus
 */
@Getter
@Setter
public class RESTSubscriber extends Thread implements SubscriberInterface {

    private CoordinatorInterface coordinator;
    private final String subscriberName;
    private final String rateUrl;
    private final String loginUrl;
    private final String serverUrl;
    private boolean connectionStatus;
    private final List<String> subscribedRateList;
    private final RestTemplate restTemplate;
    private final Logger logger = LogManager.getLogger("SubscriberLogger");
    private int startNumber = 0;

    /**
     * Constructs a `RESTSubscriber` with the given subscriber name and server URL.
     * Initializes required URLs and the RestTemplate used for HTTP requests.
     *
     * @param subscriberName The name of the subscriber.
     * @param serverUrl The base URL of the server to connect to.
     * @throws IOException If any error occurs during initialization.
     */
    public RESTSubscriber(String subscriberName, String serverUrl) throws IOException {
        logger.info("Initializing " + subscriberName + " Subscriber");
        this.subscriberName = subscriberName;
        this.serverUrl = serverUrl + "/api";
        this.rateUrl = this.serverUrl + "/rates";
        this.loginUrl = this.serverUrl + "/login";
        this.restTemplate = new RestTemplate();
        this.connectionStatus = false;
        this.subscribedRateList = new ArrayList<>();
        logger.info(subscriberName + " Subscriber initialized");
    }

    /**
     * Sets the coordinator to manage communication between this subscriber and the coordinator.
     *
     * @param coordinator The coordinator that handles subscriber events.
     */
    @Override
    public void setCoordinator(CoordinatorInterface coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * Returns the connection status of this subscriber (whether it is connected to the server).
     *
     * @return The current connection status.
     */
    @Override
    public boolean getConnectionStatus() {
        return this.connectionStatus;
    }

    /**
     * Connects the subscriber to the server using the provided credentials.
     * If the connection is successful, it starts the subscriber thread to receive updates.
     *
     * @param platformName The name of the platform to connect to.
     * @param userid The user ID for login.
     * @param password The password for login.
     * @throws IOException If any error occurs during the connection process.
     */
    @Override
    public void connect(String platformName, String userid, String password) throws IOException {
        logger.info("Connecting to {}", serverUrl);
        LoginRequest loginRequest = new LoginRequest(userid, password);
        try {
            ResponseEntity<UserAuth> userAuthResponse = restTemplate.postForEntity(this.loginUrl, loginRequest, UserAuth.class);

            if (userAuthResponse.getStatusCode() != HttpStatus.OK) {
                logger.error("Failed to connect to {}: Status code {}", serverUrl, userAuthResponse.getStatusCode());
                this.connectionStatus = false;
                return;
            }

            UserAuth loginResponse = userAuthResponse.getBody();
            if (loginResponse.getStatus().equals("success")) {
                logger.info("Login successful: {}", loginResponse.getMessage());
            } else {
                logger.warn("Login failed: Status: {}, Message: {}", loginResponse.getStatus(), loginResponse.getMessage());
                this.connectionStatus = false;
                return;
            }

            this.connectionStatus = true;
            coordinator.onConnect(platformName, connectionStatus);
            logger.info("Connected to {}", serverUrl);
            this.start();

        } catch (ResourceAccessException e) {
            logger.error("Connection failed: Unable to reach {}. Error: {}", serverUrl, e.getMessage());
            this.connectionStatus = false;
        } catch (Exception e) {
            this.connectionStatus = false;
            logger.error("Unexpected error occurred while trying to connect to {}: {}", serverUrl, e.getMessage());
        }
    }

    /**
     * Disconnects the subscriber from the server.
     *
     * @param platformName The name of the platform to disconnect from.
     * @param userid The user ID for login.
     * @param password The password for login.
     * @throws IOException If any error occurs during the disconnection process.
     */
    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        logger.info("Disconnecting from {}", serverUrl);
        this.connectionStatus = false;
        coordinator.onDisConnect(platformName, connectionStatus);
        logger.info("Disconnected from {}", serverUrl);
    }

    /**
     * Subscribes the platform to a specific rate.
     *
     * @param platformName The name of the platform to subscribe.
     * @param rateName The name of the rate to subscribe to.
     * @throws IOException If any error occurs during the subscription process.
     */
    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is subscribing to {}", platformName, rateName);
        subscribedRateList.add(platformName + "_" + rateName);
        logger.info("{} subscribed to {}", platformName, rateName);
    }

    /**
     * Unsubscribes the platform from a specific rate.
     *
     * @param platformName The name of the platform to unsubscribe.
     * @param rateName The name of the rate to unsubscribe from.
     * @throws IOException If any error occurs during the unsubscription process.
     */
    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is unsubscribing from {}", platformName, rateName);
        if (subscribedRateList.contains(platformName + "_" + rateName)) {
            subscribedRateList.remove(platformName + "_" + rateName);
        }
        logger.info("{} unsubscribed from {}", platformName, rateName);
    }

    /**
     * This method is executed in a separate thread. It continuously checks for rate updates for the subscribed rates.
     * It sends a request to the server to retrieve the latest rate information and notifies the coordinator based on
     * the current status of the rate (new, updated, or not available).
     */
    @Override
    public void run() {
        System.out.println("Start number is " + startNumber);
        while (connectionStatus) {
            for (String rateName : subscribedRateList) {
                String rateRequestURL = this.rateUrl + "/" + rateName;
                ResponseEntity<RateDto> response = null;

                // Retry mechanism for failed requests
                int retries = 0;
                boolean success = false;
                while (retries < 3 && !success) {
                    try {
                        response = restTemplate.getForEntity(rateRequestURL, RateDto.class);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            success = true;
                        } else {
                            logger.error("Failed to fetch rate: {} from {}. Status code: {}. Retrying...", rateName, rateRequestURL, response.getStatusCode());
                        }
                    } catch (ResourceAccessException e) {
                        logger.error("Connection issue while accessing {}. Error: {}. Retrying...", rateRequestURL, e.getMessage());
                    } catch (Exception e) {
                        logger.error("Unexpected error while accessing {}. Error: {}", rateRequestURL, e.getMessage());
                    }

                    // Retry delay (e.g., 2 seconds)
                    if (!success) {
                        retries++;
                        try {
                            Thread.sleep(2000); // wait for 2 seconds before retrying
                        } catch (InterruptedException e) {
                            logger.error("Sleep interrupted during retry. Error: {}", e.getMessage());
                        }
                    }
                }

                // If the request failed after retries, log and continue with the next rate
                if (!success) {
                    logger.error("Unable to fetch rate for {} after multiple retries. Skipping this rate.", rateName);
                    continue; // Skip to the next rate and continue the loop
                }

                // Process the successful response
                RateDto rateDto = response.getBody();
                switch (coordinator.onRateStatus(this.subscriberName, rateDto.getRateName())) {
                    case RateStatus.NOT_AVAILABLE:
                        coordinator.onRateAvailable(this.subscriberName, rateDto.getRateName(), rateDto);
                        break;
                    case RateStatus.AVAILABLE:
                        coordinator.onRateUpdate(this.subscriberName, rateDto.getRateName(), rateDto);
                        break;
                    case RateStatus.UPDATED:
                        coordinator.onRateUpdate(this.subscriberName, rateDto.getRateName(), rateDto);
                        break;
                }
            }

            try {
                Thread.sleep(1000); // sleep for 1 second before checking again
            } catch (InterruptedException e) {
                logger.error(this.subscriberName + "Subscriber Error: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

}
