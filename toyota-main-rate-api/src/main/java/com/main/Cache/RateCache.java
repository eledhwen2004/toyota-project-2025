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

    private final Logger logger = LogManager.getLogger("CacheLogger");
    private final IMap<String, RateDto> rawRateCache;
    private final IMap<String, RateDto> calculatedRateCache;
    private final HazelcastInstance hazelcastInstance;

    public RateCache(){
        logger.info("Rate Cache is being initialized");
        this.hazelcastInstance = Hazelcast.getHazelcastInstanceByName("hazelcast-instance");
        this.rawRateCache = hazelcastInstance.getMap("raw-rate-cache");
        this.calculatedRateCache = hazelcastInstance.getMap("calculated-rate-cache");
        logger.info("Rate Cache has been created!");
    }

    public List<RateDto> getRawRatesIfContains(String symbol){

        logger.info("Rates with \"{}\" symbol has been requested from raw rate cache",symbol);
        List<RateDto> rawRatesBySymbol = new ArrayList<>();
        Collection<RateDto> rawRateList = rawRateCache.values();
        for(RateDto rawRate : rawRateList){
            if(rawRate.getRateName().contains(symbol)){
                rawRatesBySymbol.add(rawRate);
                rawRateCache.remove(rawRate.getRateName());
            }
        }
        if(rawRatesBySymbol.isEmpty()){
            logger.info("There is no raw rates with the symbol \"{}\" in raw rate cache!",symbol);
            return null;
        }
        logger.info("Rate with {} symbol fetched from raw rate cache!",symbol);
        return rawRatesBySymbol;
    }

    public RateDto getRawRateByAllName(String rateName){
        logger.info("{} rate requested!",rateName);
        RateDto rateDto = rawRateCache.remove(rateName);
        if(rateDto == null){
            logger.info("There is no rate with name \"{}\" in raw rate cache!",rateName);
            return null;
        }
        logger.info("{} rate has been retrieved from raw rate cache",rateName);
        return rateDto;
    }

    public RateDto getCalculatedRateByName(String rateName){
        logger.info("Calculated {} rate requested!",rateName);
        RateDto rateDto = calculatedRateCache.remove(rateName);
        if(rateDto == null){
            logger.info("There is no rate with name \"{}\" in calculated rate cache!",rateName);
            return null;
        }
        logger.info("Calculated {} rate has been from retrieved from calculated rate cache",rateName);
        return rateDto;
    }

    public void updateRawRate(RateDto rawRate){
        String rawRateName = rawRate.getRateName();
        logger.info("Raw rate {} is getting updated!",rawRateName);
        rawRateCache.put(rawRateName, rawRate);
        logger.info("Raw rate {} has been updated!",rawRateName);
    }

    public void updateCalculatedRate(RateDto calculatedRate){
        if(calculatedRate == null){
            return;
        }
        String calculatedRateName = calculatedRate.getRateName();
        logger.info("Calculated rate {} is getting updated!",calculatedRateName);
        calculatedRateCache.put(calculatedRateName, calculatedRate);
        logger.info("Calculated rate {} has been updated!",calculatedRateName);
    }

    public void close(){
        this.hazelcastInstance.shutdown();
    }

}
