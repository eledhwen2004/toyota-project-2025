package com.main.Cache;

import com.main.Dto.RateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateCache {

    private final Logger cacheLogger = LogManager.getLogger("CacheLogger");
    private final Map<String,RateDto> rawRates = new ConcurrentHashMap<>();
    private final Map<String,RateDto> calculatedRates = new ConcurrentHashMap<>();

    public RateCache(String []subscriberNames,String[] rawRateNames,String[] calculatedRateNames){
        cacheLogger.info("Rate Cache is being initialized");
        RateDto rateDto = new RateDto("",0.0,0.0, Instant.now());
        for(String subscriberName : subscriberNames) {
            for (String rawRateName : rawRateNames) {
                rawRates.put(subscriberName+"_"+rawRateName, rateDto);
            }
        }
        for(String calculatedRateName : calculatedRateNames){
            calculatedRates.put(calculatedRateName,rateDto);
        }
        cacheLogger.info("Rate Cache has been created!");
    }

    public List<RateDto> getRawRatesBySymbol(String symbol){
        cacheLogger.info("Rates with \"{}\" currency has been requested",symbol);
        List<RateDto> rawRatesBySymbol = new ArrayList<>();
        for(String key : rawRates.keySet()){
            if(key.contains(symbol)){
                rawRatesBySymbol.add(rawRates.get(key));
            }
        }
        cacheLogger.info("Rates with \"{}\" currency has been retrieved from cache",symbol);
        return rawRatesBySymbol;
    }

    public RateDto getRawRateByName(String rateName){
        cacheLogger.info("{} rate requested!",rateName);
        RateDto rateDto = rawRates.get(rateName);
        cacheLogger.info("{} rate has been retrieved from cache",rateName);
        return rateDto;
    }

    public RateDto getCalculatedRateByName(String rateName){
        cacheLogger.info("Calculated {} rate requested!",rateName);
        RateDto rateDto = calculatedRates.get(rateName);
        cacheLogger.info("Calculated {} rate has been from retrieved from cache",rateName);
        return rateDto;
    }

    public void updateRawRate(String rateName, RateDto rawRate){
        cacheLogger.info("{} raw rate is getting updated!",rateName);
        rawRates.replace(rateName, rawRate);
        cacheLogger.info("{} raw rate has been updated!",rateName);
    }

    public void updateCalculatedRate(String rateName, RateDto calculatedRate){
        cacheLogger.info("{} calculated rate is getting updated!",rateName);
        calculatedRates.replace(rateName, calculatedRate);
        cacheLogger.info("{} calculated rate has been updated!",rateName);
    }

}
