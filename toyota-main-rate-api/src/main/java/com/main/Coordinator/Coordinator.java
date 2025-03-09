package com.main.Coordinator;

import com.main.Configuration.CoordinatorConfig;
import com.main.Dto.RateDto;
import com.main.RateCache.RateCache;
import com.main.Subscriber.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

public class Coordinator extends Thread implements CoordinatorInterface{

    private ApplicationContext applicationContext;
    private String [] subscriberNames;
    private String [] rateNames;
    private HashMap<String, SubscriberInterface> subscriberHashMap;
    private RateCache rateCache;

    public Coordinator(ApplicationContext applicationContext) throws IOException {
        this.applicationContext = applicationContext;
        this.subscriberNames = CoordinatorConfig.getSubscriberNames();
        this.rateNames = CoordinatorConfig.getRateNames();
        this.subscriberHashMap = new HashMap<>();
        this.rateCache = new RateCache(CoordinatorConfig.getRawRateNames(),CoordinatorConfig.getCalculatedRateNames());
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
        this.start();
    }

    @Override
    public void run() {
        while(!subscriberHashMap.isEmpty()){

        }
    }

    @Override
    public void onConnect(String platformName, Boolean status) throws IOException {
        if(status) {
            for (String rateName : rateNames) {
                this.subscriberHashMap.get(platformName).subscribe(platformName, rateName);
            }
        }
    }

    @Override
    public void onDisConnect(String platformName, Boolean status) throws IOException {
        if(!status) {
            for (String rateName : rateNames) {
                this.subscriberHashMap.get(platformName).unSubscribe(platformName, rateName);
            }
        }
    }

    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto rate) {

    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {

    }

    @Override
    public void onRateStatus(String platformName, String rateName, RateStatus rateStatus) {

    }
}
