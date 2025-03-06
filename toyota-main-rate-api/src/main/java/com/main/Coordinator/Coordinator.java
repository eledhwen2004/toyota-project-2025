package com.main.Coordinator;

import com.main.Configuration.CoordinatorConfig;
import com.main.Dto.RateDto;
import com.main.Subscriber.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;

public class Coordinator extends Thread implements CoordinatorInterface{

    private ApplicationContext applicationContext;
    private String [] subscriberNames;
    private String [] rateNames;
    private HashMap<String, SubscriberInterface> subscriberHashMap;

    public Coordinator(ApplicationContext applicationContext) throws IOException {
        this.applicationContext = applicationContext;
        this.subscriberNames = CoordinatorConfig.getSubscriberNames();
        this.rateNames = CoordinatorConfig.getRateNames();
        this.subscriberHashMap = new HashMap<>();
        for(String subscriberName : subscriberNames){
            subscriberHashMap.put(subscriberName, applicationContext.getBean(subscriberName, SubscriberInterface.class));
        }
        this.start();
    }

    @Override
    public void run() {
        try {
            for(String subscriberName : subscriberNames){
                this.subscriberHashMap.get(subscriberName).connect(subscriberName,"1234","1234");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            for(String subscriberName : subscriberNames){
                for(String rateName : rateNames)
                this.subscriberHashMap.get(subscriberName).subscribe(subscriberName,rateName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnect(String platformName, Boolean status) {

    }

    @Override
    public void onDisConnect(String platformName, Boolean status) {

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
