package com.main.Coordinator;

import com.main.ClassLoader.SubscriberClassLoader;
import com.main.Configuration.CoordinatorConfig;
import com.main.Configuration.SubscriberConfig;
import com.main.Dto.RateDto;
import com.main.Cache.RateCache;
import com.main.Kafka.Kafka;
import com.main.RateCalculator.RateCalculator;
import com.main.Dto.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.*;

/**
 * Coordinator is the central controller of the rate management system.
 * <p>
 * It is responsible for:
 * <ul>
 *   <li>Registering and connecting to REST/TCP subscribers</li>
 *   <li>Listening to rate availability and updates</li>
 *   <li>Storing raw and calculated rates in Hazelcast cache</li>
 *   <li>Publishing calculated rates to Kafka</li>
 * </ul>
 * <p>
 * This class runs as a separate thread and periodically calculates derived rates.
 */
public class Coordinator extends Thread implements CoordinatorInterface, AutoCloseable {

    private final ApplicationContext applicationContext;
    private final List<String> subscriberNames = new ArrayList<>();
    private final String[] subscribedRateNames;
    private final String[] rawRateNames;
    private final String[] calculatedRateNames;
    private final Map<String, RateStatus> rateStatusHashMap = new HashMap<>();
    private final Map<String, SubscriberInterface> subscriberHashMap = new HashMap<>();
    private final RateCache rateCache;
    private final RateCalculator rateCalculator;
    private final Logger logger = LogManager.getLogger("CoordinatorLogger");
    private final Kafka kafka;

    private final String userName = "1234";
    private final String password = "1234";

    /**
     * Initializes the Coordinator, loads subscribers and configurations, connects to subscribers,
     * initializes rate cache and calculator, and starts the processing thread.
     *
     * @param applicationContext the Spring application context
     * @throws IOException if configuration loading or subscriber setup fails
     */
    public Coordinator(ApplicationContext applicationContext) throws IOException {
        logger.info("Initializing Coordinator");
        this.applicationContext = applicationContext;

        this.SubscriberRegisterer();
        this.subscribedRateNames = CoordinatorConfig.getRawRateNames();
        this.rawRateNames = CoordinatorConfig.getRawRateNames();
        this.calculatedRateNames = CoordinatorConfig.getCalculatedRateNames();

        for (String rawRate : rawRateNames) {
            for (String subscriber : subscriberNames) {
                rateStatusHashMap.put(subscriber + "_" + rawRate, RateStatus.NOT_AVAILABLE);
            }
        }

        this.kafka = new Kafka();
        this.rateCache = new RateCache();
        this.rateCalculator = new RateCalculator(rateCache, rawRateNames, CoordinatorConfig.getDerivedRateNames());
        this.SubscriberConnector(userName, password);

        logger.info("Coordinator initialized successfully");
        this.start();
    }

    /**
     * Registers subscribers from {@code subscriber.properties} and instantiates them via reflection.
     *
     * @throws IOException if configuration file cannot be loaded
     */
    public void SubscriberRegisterer() throws IOException {
        logger.info("Subscribers are getting registered...");
        String[] serverAddresses = SubscriberConfig.getServerAddresses();

        for (String serverAddress : serverAddresses) {
            if (serverAddress.isEmpty()) continue;

            String newSubscriberName = "PF" + (subscriberNames.size() + 1);
            SubscriberInterface sub;

            if (serverAddress.startsWith("http://") || serverAddress.startsWith("https://")) {
                sub = SubscriberClassLoader.loadSubscriber("RESTSubscriber",
                        new Class[]{String.class, String.class},
                        new Object[]{newSubscriberName, serverAddress});
            } else {
                String[] parts = serverAddress.split("\\s+");
                if (parts.length == 2) {
                    try {
                        sub = SubscriberClassLoader.loadSubscriber("TCPSubscriber",
                                new Class[]{String.class, String.class, int.class},
                                new Object[]{newSubscriberName, parts[0], Integer.parseInt(parts[1])});
                    } catch (NumberFormatException e) {
                        logger.error("Invalid TCP port: {}", parts[1]);
                        continue;
                    }
                } else {
                    logger.error("Invalid TCP entry format: {}", serverAddress);
                    continue;
                }
            }

            if (sub == null) {
                logger.error("Problem occurred while registering subscriber: {}", newSubscriberName);
                continue;
            }

            subscriberNames.add(newSubscriberName);
            subscriberHashMap.put(newSubscriberName, sub);
            sub.setCoordinator(this);
            logger.info("Successfully registered subscriber: {}", newSubscriberName);
        }

        logger.info("All subscribers have been registered.");
    }

    /**
     * Connects all registered subscribers with provided credentials.
     *
     * @param userName the username
     * @param password the password
     * @throws IOException if any connection error occurs
     */
    public void SubscriberConnector(String userName, String password) throws IOException {
        logger.info("Connecting to subscribers...");
        for (String subscriberName : subscriberNames) {
            SubscriberInterface sub = subscriberHashMap.get(subscriberName);
            if (sub == null) throw new IllegalStateException("Subscriber not found: " + subscriberName);
            sub.connect(subscriberName, userName, password);
        }
    }

    /**
     * Gracefully disconnects all subscribers and shuts down the cache.
     */
    @Override
    public void close() throws Exception {
        logger.info("Coordinator shutting down...");
        for (String subscriberName : subscriberNames) {
            SubscriberInterface sub = subscriberHashMap.get(subscriberName);
            sub.disConnect(subscriberName, userName, password);
        }
        rateCache.close();
        logger.info("Shutdown complete.");
    }

    /**
     * Main thread loop for periodically calculating rates and publishing them to Kafka.
     */
    @Override
    public void run() {
        logger.info("Coordinator thread started.");
        while (!subscriberHashMap.isEmpty()) {
            try {
                sleep(1001);
            } catch (InterruptedException e) {
                logger.error("Coordinator interrupted", e);
                throw new RuntimeException(e);
            }

            for (String calculatedRateName : calculatedRateNames) {
                RateDto rateDto = rateCalculator.calculateRate(calculatedRateName);
                if (rateDto != null) {
                    rateCache.updateCalculatedRate(rateDto);
                    kafka.produceRateEvent(rateDto);
                }
            }
        }
    }

    /**
     * Callback when a subscriber successfully connects.
     */
    @Override
    public void onConnect(String platformName, Boolean status) throws IOException {
        logger.info("Connected to platform: {} | Status: {}", platformName, status);
        if (status) {
            for (String rateName : subscribedRateNames) {
                subscriberHashMap.get(platformName).subscribe(platformName, rateName);
            }
        }
    }

    /**
     * Callback when a subscriber disconnects.
     */
    @Override
    public void onDisConnect(String platformName, Boolean status) throws IOException {
        logger.info("Disconnected from platform: {} | Status: {}", platformName, status);
        if (!status) {
            for (String rateName : subscribedRateNames) {
                subscriberHashMap.get(platformName).unSubscribe(platformName, rateName);
            }
        }
    }

    /**
     * Callback for new rate availability. Updates cache and sends to Kafka.
     */
    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto rate) {
        rate.setStatus(RateStatus.AVAILABLE);
        rateStatusHashMap.put(rateName, RateStatus.AVAILABLE);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
    }

    /**
     * Callback for rate updates. Updates cache and sends to Kafka.
     */
    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {
        rateStatusHashMap.put(rateName, RateStatus.UPDATED);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
    }

    /**
     * Returns the current status of a rate.
     */
    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateStatusHashMap.get(rateName);
    }
}
