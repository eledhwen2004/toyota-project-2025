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

public class Coordinator extends Thread implements CoordinatorInterface,AutoCloseable{

    private final ApplicationContext applicationContext;
    private final String [] subscriberNames;
    private final String [] subscribedRateNames;
    private final String [] rawRateNames;
    private final String [] calculatedRateNames;
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

    public Coordinator(ApplicationContext applicationContext) throws IOException {
        logger.info("Initializing Coordinator ");
        this.applicationContext = applicationContext;
        this.subscriberNames = CoordinatorConfig.getSubscriberNames();
        this.subscribedRateNames = CoordinatorConfig.getRawRateNames();
        this.rawRateNames = CoordinatorConfig.getRawRateNames();
        this.calculatedRateNames = CoordinatorConfig.getCalculatedRateNames();
        this.rateStatusHashMap = new HashMap<>();
        for(String rawRateNames : rawRateNames) {
            for(String subscriberName : subscriberNames) {
                rateStatusHashMap.put(subscriberName + "_" + rawRateNames, RateStatus.NOT_AVAILABLE);
            }
        }
        this.subscriberHashMap = new HashMap<>();
        this.kafka = new Kafka();
        this.database = applicationContext.getBean("postgresqlDatabase",PostgresqlDatabase.class);
        this.rateCache = new RateCache();
        this.openSearchService = applicationContext.getBean("openSearchService", OpenSearchService.class);
        this.rateService = new RateServiceImpl(this.rateCache,this.database,this.rawRateNames,this.calculatedRateNames);
        this.rateCalculator = new RateCalculator(this.rateService,CoordinatorConfig.getRawRateNames(),CoordinatorConfig.getDerivedRateNames());
        this.TCPSubscriberRegisterer();
        this.RESTSubscriberRegisterer();
        this.SubscriberConnector(this.userName,this.password);

        logger.info("Coordinator initialized");
        this.start();
    }

    public void TCPSubscriberRegisterer() throws IOException {
        String [] TCPSubscriberNames = TCPSubscriberConfig.getSubscriberNames();
        String [] TCPServerAdress = TCPSubscriberConfig.getServerAdresses();
        int [] TCPServerPorts =  TCPSubscriberConfig.getPorts();
        for(int i = 0;i<TCPSubscriberNames.length;i++) {
            for(int j = 0;j<subscriberNames.length;j++) {
                if(subscriberNames[i].equals(TCPSubscriberNames[i])){
                    System.out.println("Registering Subscriber : " + subscriberNames[i]);
                    SubscriberInterface sub = SubscriberClassLoader.loadSubscriber(
                            "TCPSubscriber",
                            new Class<?>[]{String.class, String.class, int.class},
                            new Object[]{TCPSubscriberNames[i], TCPServerAdress[i], TCPServerPorts[i]}
                    );

                    if (sub == null) {
                        throw new IllegalStateException("Failed to load subscriber: " + TCPSubscriberNames[i]);
                    }

                    subscriberHashMap.put(TCPSubscriberNames[i], sub);
                    sub.setCoordinator(this);
                }
            }
        }
    }

    public void RESTSubscriberRegisterer() throws IOException {
        String[] RESTSubscriberNames = RESTSubscriberConfig.getSubscriberNames();
        String[] RESTServerAdreseses = RESTSubscriberConfig.getServerAddresses();

        for (int i = 0; i < RESTSubscriberNames.length; i++) {
            for (int j = 0; j < subscriberNames.length; j++) {
                if (subscriberNames[j].equals(RESTSubscriberNames[i])) {
                    System.out.println("Registering Subscriber : " + subscriberNames[j]);

                    SubscriberInterface sub = SubscriberClassLoader.loadSubscriber(
                            "RESTSubscriber",
                            new Class<?>[]{String.class, String.class},
                            new Object[]{RESTSubscriberNames[i], RESTServerAdreseses[i]}
                    );

                    if (sub == null) {
                        throw new IllegalStateException("Failed to load subscriber: " + RESTSubscriberNames[i]);
                    }

                    subscriberHashMap.put(RESTSubscriberNames[i], sub);
                    sub.setCoordinator(this);
                }
            }
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
                openSearchService.updateRates(kafka.consumeRateEvent());
                database.updateRateTable(kafka.consumeRateEvent());
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
        database.updateRateTable(kafka.consumeRateEvent());
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {
        logger.info("Rate updated for platform {} -- rateName {}", platformName,rateName);
        rate.setStatus(RateStatus.UPDATED);
        this.rateStatusHashMap.put(rateName, RateStatus.UPDATED);
        rateCache.updateRawRate(rate);
        kafka.produceRateEvent(rate);
        database.updateRateTable(kafka.consumeRateEvent());
    }

    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateStatusHashMap.get(rateName);
    }


}
