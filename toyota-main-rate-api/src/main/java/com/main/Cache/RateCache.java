package com.main.Cache;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.main.Dto.RateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RateCache {

    private final Logger cacheLogger = LogManager.getLogger("CacheLogger");
    private final IMap<String, RateDto> rawRateCache;
    private final IMap<String, RateDto> calculatedRateCache;

    public RateCache(){
        cacheLogger.info("Rate Cache is being initialized");
        HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName("hazelcast-instance");
        this.rawRateCache = hazelcastInstance.getMap("raw-rate-cache");
        this.calculatedRateCache = hazelcastInstance.getMap("calculated-rate-cache");
        cacheLogger.info("Rate Cache has been created!");
    }

    public List<RateDto> getRawRatesIfContains(String symbol){

        cacheLogger.info("Rates with \"{}\" symbol has been requested from raw rate cache",symbol);
        List<RateDto> rawRatesBySymbol = new ArrayList<>();
        Collection<RateDto> rawRateList = rawRateCache.values();
        for(RateDto rawRate : rawRateList){
            if(rawRate.getRateName().contains(symbol)){
                rawRatesBySymbol.add(rawRate);
            }
        }
        if(rawRatesBySymbol.isEmpty()){
            cacheLogger.info("There is no raw rates with the symbol \"{}\" in raw rate cache!",symbol);
            return null;
        }
        cacheLogger.info("Rate with {} symbol fetched from raw rate cache!",symbol);
        return rawRatesBySymbol;
    }

    public RateDto getRawRateByAllName(String rateName){
        cacheLogger.info("{} rate requested!",rateName);
        RateDto rateDto = rawRateCache.get(rateName);
        if(rateDto == null){
            cacheLogger.info("There is no rate with name \"{}\" in raw rate cache!",rateName);
            return null;
        }
        cacheLogger.info("{} rate has been retrieved from raw rate cache",rateName);
        return rateDto;
    }

    public RateDto getCalculatedRateByName(String rateName){
        cacheLogger.info("Calculated {} rate requested!",rateName);
        RateDto rateDto = calculatedRateCache.get(rateName);
        if(rateDto == null){
            cacheLogger.info("There is no rate with name \"{}\" in calculated rate cache!",rateName);
            return null;
        }
        cacheLogger.info("Calculated {} rate has been from retrieved from calculated rate cache",rateName);
        return rateDto;
    }

    public void updateRawRate(RateDto rawRate){
        String rawRateName = rawRate.getRateName();
        cacheLogger.info("Raw rate {} is getting updated!",rawRateName);
        rawRateCache.put(rawRateName, rawRate);
        cacheLogger.info("Raw rate {} has been updated!",rawRateName);
    }

    public void updateCalculatedRate(RateDto calculatedRate){
        if(calculatedRate == null){
            return;
        }
        String calculatedRateName = calculatedRate.getRateName();
        cacheLogger.info("Calculated rate {} is getting updated!",calculatedRateName);
        calculatedRateCache.put(calculatedRateName, calculatedRate);
        cacheLogger.info("Calculated rate {} has been updated!",calculatedRateName);
    }

}
