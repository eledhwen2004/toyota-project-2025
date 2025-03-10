package com.main.Cache;

import com.main.Dto.RateDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateCache {

    private final Map<String,RateDto> rawRates = new ConcurrentHashMap<>();
    private final Map<String,RateDto> calculatedRates = new ConcurrentHashMap<>();

    public RateCache(String []subscriberNames,String[] rawRateNames,String[] calculatedRateNames){
        RateDto rateDto = new RateDto("",0.0,0.0, Instant.now());
        for(String subscriberName : subscriberNames) {
            for (String rawRateName : rawRateNames) {
                rawRates.put(subscriberName+"_"+rawRateName, rateDto);
            }
        }
        for(String calculatedRateName : calculatedRateNames){
            calculatedRates.put(calculatedRateName,rateDto);
        }
    }

    public List<RateDto> getRawRatesBySymbol(String symbol){
        List<RateDto> rawRatesBySymbol = new ArrayList<>();
        for(String key : rawRates.keySet()){
            if(key.contains(symbol)){
                rawRatesBySymbol.add(rawRates.get(key));
            }
        }
        return rawRatesBySymbol;
    }

    public RateDto getRawRateByName(String rateName){
        return rawRates.get(rateName);
    }

    public RateDto getCalculatedRateBySymbol(String symbol){
        return calculatedRates.get(symbol);
    }

    public void updateRawRate(String rateName, RateDto rawRate){
        rawRates.replace(rateName, rawRate);
    }

    public void updateCalculatedRate(String rateName, RateDto calculatedRate){
        calculatedRates.replace(rateName, calculatedRate);
    }

}
