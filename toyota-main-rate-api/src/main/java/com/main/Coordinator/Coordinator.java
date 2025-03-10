package com.main.Coordinator;

import com.main.Configuration.CoordinatorConfig;
import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import com.main.Cache.RateCache;
import com.main.RateCalculator.RateCalculator;
import com.main.Subscriber.RateStatus;
import com.main.Subscriber.SubscriberInterface;
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

    public Coordinator(ApplicationContext applicationContext) throws IOException {
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
        this.start();
    }

    @Override
    public void run() {
        while(!subscriberHashMap.isEmpty()){
            try {
                sleep(1001);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for(String rateName : calculatedRateNames){
                RateDto rateDto = rateCalculator.calculateRate(rateName);
                rateCache.updateCalculatedRate(rateName,rateDto);
                System.out.println(rateDto.getRateName() + "|"
                                        + rateDto.getAsk() + "|"
                                        + rateDto.getBid() + "|"
                                        + rateDto.getTimestamp());
            };
            for(String subscriberName : subscriberNames){
                for(String rateName : subscribedRateNames) {
                    RateDto rateDto = rateCache.getRawRateByName(subscriberName+"_"+rateName);
                    System.out.println(rateDto.getRateName() + "|"
                                          + rateDto.getBid() + "|"
                                          + rateDto.getAsk() + "|"
                                          + rateDto.getTimestamp());
                }
            }
        }
    }

    @Override
    public void onConnect(String platformName, Boolean status) throws IOException {
        if(status) {
            for (String rateName : subscribedRateNames) {
                this.subscriberHashMap.get(platformName).subscribe(platformName, rateName);
            }
        }
    }

    @Override
    public void onDisConnect(String platformName, Boolean status) throws IOException {
        if(!status) {
            for (String rateName : subscribedRateNames) {
                this.subscriberHashMap.get(platformName).unSubscribe(platformName, rateName);
            }
        }
    }

    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto rate) {
        rate.setStatus(RateStatus.AVAILABLE);
        rateCache.updateRawRate(rateName,rate);
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto rate) {
        rate.setStatus(RateStatus.UPDATED);
        rateCache.updateRawRate(rateName,rate);
    }

    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        return rateCache.getRawRateByName(rateName).getStatus();
    }
}
