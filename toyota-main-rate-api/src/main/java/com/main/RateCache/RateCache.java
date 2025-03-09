package com.main.RateCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RateCache {
    private HashMap<String,List<RawRate>> rawRates;
    private HashMap<String,List<CalculatedRate>> calculatedRates;

    public RateCache(String[] rawRateNames,String[] calculatedRateNames) {
        rawRates = new HashMap<>();
        calculatedRates = new HashMap<>();
        for (String rawRateName : rawRateNames) {
            rawRates.put(rawRateName, new ArrayList<>());
        }
        for (String calculatedRateName : calculatedRateNames) {
            calculatedRates.put(calculatedRateName, new ArrayList<>());
        }
    }

    public HashMap<String, List<RawRate>> getRawRates() {
        return rawRates;
    }

    public HashMap<String, List<CalculatedRate>> getCalculatedRates() {
        return calculatedRates;
    }

}
