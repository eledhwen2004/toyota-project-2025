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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST-based subscriber implementation that connects to an external platform,
 * authenticates via REST API, subscribes to rates, and polls rate data periodically.
 * <p>
 * This class communicates with a remote platform using standard HTTP endpoints
 * for login and rate retrieval, and interacts with the {@link CoordinatorInterface}
 * to report events such as connection, disconnection, rate availability, and updates.
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
     * Constructs a new {@code RESTSubscriber} instance with the specified name and server URL.
     *
     * @param subscriberName the name of the subscriber
     * @param serverUrl      the base URL of the external REST API (e.g., "http://localhost:8080")
     * @throws IOException if initialization fails
     */
    public RESTSubscriber(String subscriberName, String serverUrl) throws IOException {
        logger.info("Initializing {} Subscriber", subscriberName);
        this.subscriberName = subscriberName;
        this.serverUrl = serverUrl + "/api";
        this.rateUrl = this.serverUrl + "/rates";
        this.loginUrl = this.serverUrl + "/login";
        this.restTemplate = new RestTemplate();
        this.connectionStatus = false;
        this.subscribedRateList = new ArrayList<>();
        logger.info("{} Subscriber initialized", subscriberName);
    }

    /**
     * Sets the coordinator reference for event communication.
     */
    @Override
    public void setCoordinator(CoordinatorInterface coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * Connects to the external REST API platform by sending login credentials.
     *
     * @param platformName platform identifier
     * @param userid       login username
     * @param password     login password
     * @throws IOException if connection fails
     */
    @Override
    public void connect(String platformName, String userid, String password) throws IOException {
        logger.info("Connecting to {}", serverUrl);
        LoginRequest loginRequest = new LoginRequest(userid, password);
        ResponseEntity<UserAuth> userAuthResponse = restTemplate.postForEntity(this.loginUrl, loginRequest, UserAuth.class);
        UserAuth loginResponse = userAuthResponse.getBody();

        if (loginResponse.getStatus().equals("success")) {
            System.out.println(loginResponse.getMessage());
        } else {
            System.out.println("status : " + loginResponse.getStatus());
            System.out.println("message : " + loginResponse.getMessage());
            return;
        }

        this.connectionStatus = true;
        coordinator.onConnect(platformName, connectionStatus);
        logger.info("Connected to {}", serverUrl);
        this.start();
    }

    /**
     * Disconnects from the external REST platform.
     *
     * @param platformName subscriber name
     * @param userid       login username
     * @param password     login password
     * @throws IOException if disconnection fails
     */
    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        logger.info("Disconnecting from {}", serverUrl);
        this.connectionStatus = false;
        coordinator.onDisConnect(platformName, connectionStatus);
        logger.info("Disconnected from {}", serverUrl);
    }

    /**
     * Adds a rate to the subscription list.
     */
    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is subscribing to {}", platformName, rateName);
        subscribedRateList.add(platformName + "_" + rateName);
        logger.info("{} subscribed to {}", platformName, rateName);
    }

    /**
     * Removes a rate from the subscription list.
     */
    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is unsubscribing from {}", platformName, rateName);
        subscribedRateList.remove(platformName + "_" + rateName);
        logger.info("{} unsubscribed from {}", platformName, rateName);
    }

    /**
     * Polling thread that fetches rate data every second and notifies the coordinator of any changes.
     */
    @Override
    public void run() {
        System.out.println("Start number is " + startNumber);
        while (connectionStatus) {
            for (String rateName : subscribedRateList) {
                String rateRequestURL = this.rateUrl + "/" + rateName;
                ResponseEntity<RateDto> response = restTemplate.getForEntity(rateRequestURL, RateDto.class);
                RateDto rateDto = response.getBody();
                switch (coordinator.onRateStatus(this.subscriberName, rateDto.getRateName())) {
                    case RateStatus.NOT_AVAILABLE:
                        coordinator.onRateAvailable(this.subscriberName, rateDto.getRateName(), rateDto);
                        break;
                    case RateStatus.AVAILABLE:
                    case RateStatus.UPDATED:
                        coordinator.onRateUpdate(this.subscriberName, rateDto.getRateName(), rateDto);
                        break;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("{} Subscriber Error : {}", subscriberName, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
