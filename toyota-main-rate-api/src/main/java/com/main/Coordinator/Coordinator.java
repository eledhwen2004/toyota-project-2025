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
        this.applicationContext = applicationContext;
        this.SubscriberRegisterer();
        this.subscribedRateNames = CoordinatorConfig.getRawRateNames();
        this.rawRateNames = CoordinatorConfig.getRawRateNames();
        this.calculatedRateNames = CoordinatorConfig.getCalculatedRateNames();
        for(String rawRateNames : rawRateNames) {
            for(String subscriberName : subscriberNames) {
                rateStatusHashMap.put(subscriberName + "_" + rawRateNames, RateStatus.NOT_AVAILABLE);
            }
        }
        this.kafka = new Kafka();
        this.rateCache = new RateCache();
        this.rateCalculator = new RateCalculator(this.rateCache,CoordinatorConfig.getRawRateNames(),CoordinatorConfig.getDerivedRateNames());
        this.SubscriberConnector(this.userName,this.password);

        logger.info("Coordinator initialized");
        this.start();
    }

    public void SubscriberRegisterer() throws IOException {
        String [] ServerAddresses = SubscriberConfig.getServerAddresses();
        for (String serverAddress : ServerAddresses) {
            if (serverAddress.isEmpty()) continue;

            String newSubscriberName = "PF" + (subscriberNames.size() + 1);
            System.out.println("Registering Subscriber : " + newSubscriberName);
            SubscriberInterface sub = null;

            if (serverAddress.startsWith("http://") || serverAddress.startsWith("https://")) {
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
                        sub = SubscriberClassLoader.loadSubscriber(
                                "TCPSubscriber",
                                new Class<?>[]{String.class, String.class, int.class},
                                new Object[]{newSubscriberName, host, port}
                        );
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid TCP port: " + parts[1]);
                    }
                } else {
                    System.out.println("Invalid TCP entry format: " + serverAddress);
                }
            }
            if(sub == null) {
                logger.error("Problem occurred while registering subscriber : {}\n", newSubscriberName);
                continue;
            }
            this.subscriberNames.add(newSubscriberName);
            subscriberHashMap.put(newSubscriberName, sub);
            sub.setCoordinator(this);

        }
    }

    public void SubscriberConnector(String userName,String password) throws IOException {
        try {
            for(String subscriberName : subscriberNames){
                SubscriberInterface sub = this.subscriberHashMap.get(subscriberName);
                if(sub == null){
                    throw new IllegalStateException("Failed to get subscriber from HashMap: " + subscriberName);
                }
                sub.connect(subscriberName,userName,password);
                System.out.println(subscriberName + " connected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void close() throws Exception {
        for(String subscriberName : subscriberNames){
            SubscriberInterface sub = this.subscriberHashMap.get(subscriberName);
            sub.disConnect(subscriberName,"1234","1234");
            subscriberHashMap.remove(subscriberName);
        }
        rateCache.close();
    }

    @Override
    public void run() {
        logger.info("Coordinator is running");
        while(!subscriberHashMap.isEmpty()){
            try {
                sleep(1001);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Calculates all rates and adds calculated ones to database
            for(String calculatedRateName : calculatedRateNames){
                RateDto rateDto = rateCalculator.calculateRate(calculatedRateName);
                if(rateDto == null){
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
            for (String rateName : subscribedRateNames) {
                this.subscriberHashMap.get(platformName).subscribe(platformName, rateName);
            }
        }
    }

    @Override
    public void onDisConnect(String platformName, Boolean status) throws IOException {
        logger.info("Disconnected from platform {} -- status {}", platformName,status);
        if(!status) {
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
        logger.info("Rate updated for platform {} -- rateName {}", platformName,rateName);
        rate.setStatus(RateStatus.UPDATED);
        this.rateStatusHashMap.put(rateName, RateStatus.UPDATED);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
    }

    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateStatusHashMap.get(rateName);
    }


}
