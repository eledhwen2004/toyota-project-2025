package com.main.Coordinator;

import com.main.Configuration.CoordinatorConfig;
import com.main.Dto.RateDto;
import com.main.Cache.RateCache;
import com.main.RateCalculator.RateCalculator;
import com.main.Dto.RateStatus;
import com.main.Repository.RateRepository;
import com.main.Subscriber.SubscriberInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;

public class Coordinator extends Thread implements CoordinatorInterface{

    private ApplicationContext applicationContext;
    private String [] subscriberNames;
    private String [] subscribedRateNames;
    private String [] calculatedRateNames;
    private HashMap<String, SubscriberInterface> subscriberHashMap;
    private RateCache rateCache;
    private RateCalculator rateCalculator;
    private final Logger logger = LogManager.getLogger("CoordinatorLogger");


    public Coordinator(ApplicationContext applicationContext) throws IOException {
        logger.info("Initializing Coordinator ");
        this.applicationContext = applicationContext;
        this.subscriberNames = CoordinatorConfig.getSubscriberNames();
        this.subscribedRateNames = CoordinatorConfig.getRateNames();
        this.calculatedRateNames = CoordinatorConfig.getCalculatedRateNames();
        this.subscriberHashMap = new HashMap<>();
        this.rateCache = new RateCache(this.subscriberNames,CoordinatorConfig.getRawRateNames(),CoordinatorConfig.getCalculatedRateNames());
        this.rateCalculator = new RateCalculator(this.rateCache,CoordinatorConfig.getRawRateNames(),CoordinatorConfig.getDerivedRateNames());
        for(String subscriberName : subscriberNames){
            subscriberHashMap.put(subscriberName, applicationContext.getBean(subscriberName, SubscriberInterface.class));
            subscriberHashMap.get(subscriberName).setCoordinator(this);
        }
        try {
            for(String subscriberName : subscriberNames){
                this.subscriberHashMap.get(subscriberName).connect(subscriberName,"1234","1234");
                System.out.println(subscriberName + " connected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Coordinator initialized");
        this.start();
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
            for(String rateName : calculatedRateNames){
                RateDto rateDto = rateCalculator.calculateRate(rateName);
                rateCache.updateCalculatedRate(rateName,rateDto);
            };
            for(String subscriberName : subscriberNames){
                for(String rateName : subscribedRateNames) {
                    RateDto rateDto = rateCache.getRawRateByName(subscriberName+"_"+rateName);
                }
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
        rateCache.updateRawRate(rateName,rate);
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {
        logger.info("Rate updated for platform {} -- rateName {}", platformName,rateName);
        rate.setStatus(RateStatus.UPDATED);
        rateCache.updateRawRate(rateName,rate);
    }

    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateCache.getRawRateByName(rateName).getStatus();
    }
}
