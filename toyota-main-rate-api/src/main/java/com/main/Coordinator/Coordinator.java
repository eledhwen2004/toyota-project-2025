package com.main.Coordinator;

import com.main.ClassLoader.SubscriberClassLoader;
import com.main.Configuration.CoordinatorConfig;
import com.main.Database.PostgresqlDatabase;
import com.main.Dto.RateDto;
import com.main.Cache.RateCache;
import com.main.Entity.RateEntity;
import com.main.Kafka.RateEvent.RateEventProducer;
import com.main.RateCalculator.RateCalculator;
import com.main.Dto.RateStatus;
import com.main.Services.RateService;
import com.main.Services.RateServiceInterface;
import com.main.Subscriber.SubscriberInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;

public class Coordinator extends Thread implements CoordinatorInterface{

    private final ApplicationContext applicationContext;
    private String [] subscriberNames;
    private String [] subscribedRateNames;
    private String [] rawRateNames;
    private String [] calculatedRateNames;
    private HashMap<String, RateStatus> rateStatusHashMap;
    private final HashMap<String, SubscriberInterface> subscriberHashMap;
    private final RateCache rateCache;
    private final RateCalculator rateCalculator;
    private final RateServiceInterface rateService;
    private final Logger logger = LogManager.getLogger("CoordinatorLogger");
    private final RateEventProducer rateEventProducer;
    private final PostgresqlDatabase database;

    public Coordinator(ApplicationContext applicationContext) throws IOException {
        logger.info("Initializing Coordinator ");
        this.applicationContext = applicationContext;
        this.subscriberNames = CoordinatorConfig.getSubscriberNames();
        this.subscribedRateNames = CoordinatorConfig.getRateNames();
        this.rawRateNames = CoordinatorConfig.getRawRateNames();
        this.calculatedRateNames = CoordinatorConfig.getCalculatedRateNames();
        this.rateStatusHashMap = new HashMap<>();
        for(String rawRateNames : rawRateNames) {
            for(String subscriberName : subscriberNames) {
                rateStatusHashMap.put(subscriberName + "_" + rawRateNames, RateStatus.NOT_AVAILABLE);
            }
        }
        this.subscriberHashMap = new HashMap<>();
        this.rateEventProducer = applicationContext.getBean("rateEventProducer", RateEventProducer.class);
        this.database = applicationContext.getBean("postgresqlDatabase",PostgresqlDatabase.class);
        this.rateCache = new RateCache();
        this.rateService = new RateService(this.rateCache,this.database,this.rawRateNames,this.calculatedRateNames);
        this.rateCalculator = new RateCalculator(this.rateService,CoordinatorConfig.getRawRateNames(),CoordinatorConfig.getDerivedRateNames());
        for(String subscriberName : subscriberNames){
            subscriberHashMap.put(subscriberName, SubscriberClassLoader.loadSubscriber(subscriberName + "Subscriber"));
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

            // Calculates all rates and adds calculated ones to database
            for(String calculatedRateName : calculatedRateNames){
                RateDto rateDto = rateCalculator.calculateRate(calculatedRateName);
                if(rateDto == null){
                    continue;
                }
                rateCache.updateCalculatedRate(rateDto);
                rateEventProducer.produceRateEvent(rateDto);
                database.updateRateTable();
            };

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
        System.out.println("2");
        rateEventProducer.produceRateEvent(rate);
        database.updateRateTable();
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {
        logger.info("Rate updated for platform {} -- rateName {}", platformName,rateName);
        rate.setStatus(RateStatus.UPDATED);
        this.rateStatusHashMap.put(rateName, RateStatus.UPDATED);
        rateCache.updateRawRate(rate);
        System.out.println("3");
        rateEventProducer.produceRateEvent(rate);
        database.updateRateTable();
    }

    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateStatusHashMap.get(rateName);
    }
}
