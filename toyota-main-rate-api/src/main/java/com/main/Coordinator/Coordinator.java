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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Coordinator extends Thread implements CoordinatorInterface,AutoCloseable{

    private final ApplicationContext applicationContext;
    private final List<String> subscriberNames = new ArrayList<>();
    private final String [] subscribedRateNames;
    private final String [] rawRateNames;
    private final String [] calculatedRateNames;
    private final HashMap<String, RateStatus> rateStatusHashMap = new HashMap<>();
    private final HashMap<String, SubscriberInterface> subscriberHashMap = new HashMap<>();;
    private final RateCache rateCache;
    private final RateCalculator rateCalculator;
    private final Logger logger = LogManager.getLogger("CoordinatorLogger");
    private final Kafka kafka;

    private final String userName = "1234";
    private final String password = "1234";

    public Coordinator(ApplicationContext applicationContext) throws IOException {
        logger.info("Initializing Coordinator ");
        logger.debug("User credentials set: username={}, password={}", userName, password);
        this.applicationContext = applicationContext;
        this.SubscriberRegisterer();
        this.subscribedRateNames = CoordinatorConfig.getRawRateNames();
        this.rawRateNames = CoordinatorConfig.getRawRateNames();
        this.calculatedRateNames = CoordinatorConfig.getCalculatedRateNames();

        logger.debug("Subscribed rate names: {}", (Object) subscribedRateNames);
        logger.debug("Calculated rate names: {}", (Object) calculatedRateNames);

        for(String rawRateNames : rawRateNames) {
            for(String subscriberName : subscriberNames) {
                rateStatusHashMap.put(subscriberName + "_" + rawRateNames, RateStatus.NOT_AVAILABLE);
            }
        }
        this.kafka = new Kafka();
        this.rateCache = new RateCache();
        this.rateCalculator = new RateCalculator(this.rateCache,CoordinatorConfig.getRawRateNames(),CoordinatorConfig.getDerivedRateNames());
        this.SubscriberConnector(this.userName,this.password);
        logger.info("Coordinator initialized successfully");
        this.start();
    }

    public void SubscriberRegisterer() throws IOException {
        logger.info("Subscriber are getting registered...");
        String [] ServerAddresses = SubscriberConfig.getServerAddresses();
        logger.debug("Fetched server addresses: {}", (Object) ServerAddresses);
        for (String serverAddress : ServerAddresses) {
            if (serverAddress.isEmpty()) continue;

            String newSubscriberName = "PF" + (subscriberNames.size() + 1);
            logger.info("Attempting to register Subscriber: {}", newSubscriberName);
            SubscriberInterface sub = null;

            if (serverAddress.startsWith("http://") || serverAddress.startsWith("https://")) {
                logger.debug("Subscriber {} is a REST subscriber with address {}", newSubscriberName, serverAddress);
                sub = SubscriberClassLoader.loadSubscriber(
                        "RESTSubscriber",
                        new Class<?>[]{String.class, String.class},
                        new Object[]{newSubscriberName, serverAddress}
                );
            } else {
                String[] parts = serverAddress.split("\\s+");
                if (parts.length == 2) {
                    try {
                        String host = parts[0];
                        int port = Integer.parseInt(parts[1]);
                        logger.debug("Subscriber {} is a TCP subscriber with host {} and port {}", newSubscriberName, host, port);
                        sub = SubscriberClassLoader.loadSubscriber(
                                "TCPSubscriber",
                                new Class<?>[]{String.class, String.class, int.class},
                                new Object[]{newSubscriberName, host, port}
                        );
                    } catch (NumberFormatException e) {
                        logger.error("Invalid TCP port: {}", parts[1]);
                    }
                } else {
                    logger.error("Invalid TCP entry format: {}", serverAddress);
                }
            }
            if(sub == null) {
                logger.error("Problem occurred while registering subscriber : {}", newSubscriberName);
                continue;
            }
            this.subscriberNames.add(newSubscriberName);
            subscriberHashMap.put(newSubscriberName, sub);
            sub.setCoordinator(this);
            logger.info("Successfully registered subscriber: {}", newSubscriberName);
        }
        logger.info("Subscribers are successfully registered");
    }

    public void SubscriberConnector(String userName,String password) throws IOException {
        logger.info("Connecting to subscribers...");
        try {
            for(String subscriberName : subscriberNames){
                logger.info("Connecting subscriber: {}", subscriberName);
                SubscriberInterface sub = this.subscriberHashMap.get(subscriberName);
                if(sub == null){
                    throw new IllegalStateException("Failed to get subscriber from HashMap: " + subscriberName);
                }
                sub.connect(subscriberName,userName,password);
                logger.info("{} connected successfully", subscriberName);
            }
        } catch (IOException e) {
            logger.error("IOException occurred during subscriber connection: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws Exception {
        logger.info("Coordinator shutting down. Disconnecting subscribers...");
        for(String subscriberName : subscriberNames){
            SubscriberInterface sub = this.subscriberHashMap.get(subscriberName);
            logger.info("Disconnected subscriber: {}", subscriberName);
            sub.disConnect(subscriberName,"1234","1234");
            subscriberHashMap.remove(subscriberName);
        }
        rateCache.close();
        logger.info("Coordinator shutdown complete");
    }

    @Override
    public void run() {
        logger.info("Coordinator is running");
        while(!subscriberHashMap.isEmpty()){
            try {
                sleep(1001);
            } catch (InterruptedException e) {
                logger.error("Main loop interrupted: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }

            // Calculates all rates and adds calculated ones to database
            for(String calculatedRateName : calculatedRateNames){
                RateDto rateDto = rateCalculator.calculateRate(calculatedRateName);
                if(rateDto == null){
                    logger.warn("Calculated rate is null for: {}", calculatedRateName);
                    continue;
                }
                rateCache.updateCalculatedRate(rateDto);
                kafka.produceRateEvent(rateDto);
            }

        }
    }

    @Override
    public void onConnect(String platformName, Boolean status) throws IOException {
        logger.info("Connected to platform {} -- status {}", platformName,status);
        if(status) {
            logger.debug("Subscribing to rates for platform: {}", platformName);
            for (String rateName : subscribedRateNames) {
                this.subscriberHashMap.get(platformName).subscribe(platformName, rateName);
            }
        }
    }

    @Override
    public void onDisConnect(String platformName, Boolean status) throws IOException {
        logger.info("Disconnected from platform {} -- status {}", platformName,status);
        if(!status) {
            logger.debug("Unsubscribing from rates for platform: {}", platformName);
            for (String rateName : subscribedRateNames) {
                this.subscriberHashMap.get(platformName).unSubscribe(platformName, rateName);
            }
        }
    }

    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto rate) {
        logger.info("Rate available for platform {} -- rateName {}", platformName,rateName);
        rate.setStatus(RateStatus.AVAILABLE);
        this.rateStatusHashMap.put(rateName, RateStatus.AVAILABLE);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {
        logger.info("Rate updated for platform {} -- rateName {}", platformName, rateName);
        this.rateStatusHashMap.put(rateName, RateStatus.UPDATED);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
    }

    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateStatusHashMap.get(rateName);
    }


}
