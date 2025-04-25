package com.main.Coordinator;

import com.main.ClassLoader.SubscriberClassLoader;
import com.main.Configuration.CoordinatorConfig;
import com.main.Configuration.RESTSubscriberConfig;
import com.main.Configuration.TCPSubscriberConfig;
import com.main.Database.PostgresqlDatabase;
import com.main.Dto.RateDto;
import com.main.Cache.RateCache;
import com.main.Kafka.Kafka;
import com.main.OpenSearch.OpenSearchService;
import com.main.RateCalculator.RateCalculator;
import com.main.Dto.RateStatus;
import com.main.Services.RateServiceImpl;
import com.main.Services.RateServiceInterface;
import com.main.Subscriber.SubscriberInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;

/**
 * The Coordinator class is responsible for managing subscribers, handling rate calculations,
 * and updating systems such as Kafka, PostgreSQL, and OpenSearch.
 * It continuously runs in a separate thread, managing the lifecycle of subscriber connections,
 * rate availability, and rate updates.
 */
public class Coordinator extends Thread implements CoordinatorInterface, AutoCloseable {

    private final String[] subscriberNames;
    private final String[] subscribedRateNames;
    private final String[] rawRateNames;
    private final String[] calculatedRateNames;
    private final HashMap<String, RateStatus> rateStatusHashMap;
    private final HashMap<String, SubscriberInterface> subscriberHashMap;
    private final RateCache rateCache;
    private final RateCalculator rateCalculator;
    private final RateServiceInterface rateService;
    private final Logger logger = LogManager.getLogger("CoordinatorLogger");
    private final Kafka kafka;
    private final PostgresqlDatabase database;
    private final OpenSearchService openSearchService;

    private final String userName = "1234";
    private final String password = "1234";

    /**
     * Constructs a Coordinator object and initializes the necessary components
     * including subscribers, rate services, Kafka, PostgreSQL, OpenSearch, and more.
     * It also starts the coordinator thread.
     *
     * @param applicationContext The Spring application context to retrieve the necessary beans.
     * @throws IOException If there is an error while registering subscribers or loading configurations.
     */
    public Coordinator(ApplicationContext applicationContext) throws IOException {
        logger.info("Initializing Coordinator ");
        this.subscriberNames = CoordinatorConfig.getSubscriberNames();
        this.subscribedRateNames = CoordinatorConfig.getRawRateNames();
        this.rawRateNames = CoordinatorConfig.getRawRateNames();
        this.calculatedRateNames = CoordinatorConfig.getCalculatedRateNames();
        this.rateStatusHashMap = new HashMap<>();
        for (String rawRateNames : rawRateNames) {
            for (String subscriberName : subscriberNames) {
                rateStatusHashMap.put(subscriberName + "_" + rawRateNames, RateStatus.NOT_AVAILABLE);
            }
        }
        this.subscriberHashMap = new HashMap<>();
        this.kafka = new Kafka();
        this.database = applicationContext.getBean("postgresqlDatabase", PostgresqlDatabase.class);
        this.rateCache = new RateCache();
        this.openSearchService = applicationContext.getBean("openSearchService", OpenSearchService.class);
        this.rateService = new RateServiceImpl(this.rateCache, this.database, this.rawRateNames, this.calculatedRateNames);
        this.rateCalculator = new RateCalculator(this.rateService, CoordinatorConfig.getRawRateNames(), CoordinatorConfig.getDerivedRateNames());
        this.TCPSubscriberRegisterer();
        this.RESTSubscriberRegisterer();
        this.SubscriberConnector(this.userName, this.password);

        logger.info("Coordinator initialized");
        this.start();
    }

    /**
     * Registers TCP subscribers by loading them through the SubscriberClassLoader.
     * Subscribers are connected to the corresponding TCP server addresses and ports.
     *
     * @throws IOException If there is an error while registering TCP subscribers.
     */
    public void TCPSubscriberRegisterer() throws IOException {
        String[] TCPSubscriberNames = TCPSubscriberConfig.getSubscriberNames();
        String[] TCPServerAdress = TCPSubscriberConfig.getServerAdresses();
        int[] TCPServerPorts = TCPSubscriberConfig.getPorts();
        for (int i = 0; i < TCPSubscriberNames.length; i++) {
            for (int j = 0; j < subscriberNames.length; j++) {
                if (subscriberNames[i].equals(TCPSubscriberNames[i])) {
                    logger.info("Registering Subscriber : " + subscriberNames[j]);
                    SubscriberInterface sub = SubscriberClassLoader.loadSubscriber(
                            "TCPSubscriber",
                            new Class<?>[]{String.class, String.class, int.class},
                            new Object[]{TCPSubscriberNames[i], TCPServerAdress[i], TCPServerPorts[i]}
                    );

                    subscriberHashMap.put(TCPSubscriberNames[i], sub);
                    sub.setCoordinator(this);
                }
            }
        }
    }

    /**
     * Registers REST subscribers by loading them through the SubscriberClassLoader.
     * Subscribers are connected to the corresponding REST server addresses.
     *
     * @throws IOException If there is an error while registering REST subscribers.
     */
    public void RESTSubscriberRegisterer() throws IOException {
        String[] RESTSubscriberNames = RESTSubscriberConfig.getSubscriberNames();
        String[] RESTServerAdreseses = RESTSubscriberConfig.getServerAddresses();

        for (int i = 0; i < RESTSubscriberNames.length; i++) {
            for (int j = 0; j < subscriberNames.length; j++) {
                if (subscriberNames[j].equals(RESTSubscriberNames[i])) {
                    logger.info("Registering Subscriber : " + subscriberNames[j]);

                    SubscriberInterface sub = SubscriberClassLoader.loadSubscriber(
                            "RESTSubscriber",
                            new Class<?>[]{String.class, String.class},
                            new Object[]{RESTSubscriberNames[i], RESTServerAdreseses[i]}
                    );

                    subscriberHashMap.put(RESTSubscriberNames[i], sub);
                    sub.setCoordinator(this);
                }
            }
        }
    }

    /**
     * Connects all registered subscribers using the provided username and password.
     *
     * @param userName The username to authenticate the subscribers.
     * @param password The password to authenticate the subscribers.
     */
    public void SubscriberConnector(String userName, String password) {
        try {
            for (String subscriberName : subscriberNames) {
                SubscriberInterface sub = this.subscriberHashMap.get(subscriberName);
                if (sub == null) {
                    logger.warn("Subscriber : {} not found", subscriberName);
                    throw new IllegalStateException("Failed to get subscriber from HashMap: " + subscriberName);
                }
                sub.connect(subscriberName, userName, password);
                if (!sub.getConnectionStatus()) {
                    logger.warn(subscriberName + " couldn't connected");
                    subscriberHashMap.remove(subscriberName);
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down the coordinator and disconnects all subscribers.
     * Releases resources and ensures the program shuts down gracefully.
     *
     * @throws Exception If an error occurs while closing resources.
     */
    @Override
    public void close() throws Exception {
        logger.info("Program is shutting down");
        for (String subscriberName : subscriberHashMap.keySet()) {
            SubscriberInterface sub = this.subscriberHashMap.get(subscriberName);
            sub.disConnect(subscriberName, "1234", "1234");
            subscriberHashMap.remove(subscriberName);
        }
        rateCache.close();
        logger.info("Program is shut down");
    }

    /**
     * Runs the coordinator in a separate thread. It calculates rates periodically
     * and updates the systems (Kafka, PostgreSQL, OpenSearch) with the calculated rates.
     */
    @Override
    public void run() {
        logger.info("Coordinator is running");
        while (!subscriberHashMap.isEmpty()) {
            try {
                sleep(1001);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Calculates all rates and adds calculated ones to database
            for (String calculatedRateName : calculatedRateNames) {
                RateDto rateDto = rateCalculator.calculateRate(calculatedRateName);
                if (rateDto == null) {
                    continue;
                }
                rateCache.updateCalculatedRate(rateDto);
                kafka.produceRateEvent(rateDto);
                openSearchService.updateRates(kafka.consumeRateEvent());
                database.updateRateTable(kafka.consumeRateEvent());
            }
        }
    }

    /**
     * Called when a subscriber successfully connects to the platform.
     * Subscribes to the rates for the platform.
     *
     * @param platformName The name of the platform.
     * @param status       The connection status.
     */
    @Override
    public void onConnect(String platformName, Boolean status) {
        logger.info("Connected to platform {} -- status {}", platformName, status);
        if (status) {
            for (String rateName : subscribedRateNames) {
                try {
                    this.subscriberHashMap.get(platformName).subscribe(platformName, rateName);
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Called when a subscriber disconnects from the platform.
     * Unsubscribes from the rates for the platform.
     *
     * @param platformName The name of the platform.
     * @param status       The disconnection status.
     */
    @Override
    public void onDisConnect(String platformName, Boolean status) {
        logger.info("Disconnected from platform {} -- status {}", platformName, status);
        if (!status) {
            for (String rateName : subscribedRateNames) {
                try {
                    this.subscriberHashMap.get(platformName).unSubscribe(platformName, rateName);
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Called when a rate becomes available for a platform.
     * Updates the rate status and the systems with the new rate.
     *
     * @param platformName The name of the platform.
     * @param rateName     The name of the rate.
     * @param rate         The rate DTO containing rate data.
     */
    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto rate) {
        logger.info("Rate available for platform {} -- rateName {}", platformName, rateName);
        rate.setStatus(RateStatus.AVAILABLE);
        this.rateStatusHashMap.put(rateName, RateStatus.AVAILABLE);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
        database.updateRateTable(kafka.consumeRateEvent());
    }

    /**
     * Called when a rate is updated for a platform.
     * Updates the rate status and the systems with the new rate.
     *
     * @param platformName The name of the platform.
     * @param rateName     The name of the rate.
     * @param rate         The rate DTO containing updated rate data.
     */
    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {
        logger.info("Rate updated for platform {} -- rateName {}", platformName, rateName);
        rate.setStatus(RateStatus.UPDATED);
        this.rateStatusHashMap.put(rateName, RateStatus.UPDATED);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
        database.updateRateTable(kafka.consumeRateEvent());
    }

    /**
     * Returns the current status of a rate for a given platform.
     *
     * @param platformName The name of the platform.
     * @param rateName     The name of the rate.
     * @return The status of the rate.
     */
    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateStatusHashMap.get(rateName);
    }
}
