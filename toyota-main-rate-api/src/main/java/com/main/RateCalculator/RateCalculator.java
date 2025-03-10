package com.main.RateCalculator;

import com.main.Dto.RateDto;
import com.main.Cache.RateCache;

import java.util.List;

public class RateCalculator {
    private RateCache rateCache;
    private String [] rawRates;
    private String [] derivedRates;

    public RateCalculator(RateCache rateCache,String [] rawRates,String [] derivedRates) {
        this.rawRates = rawRates;
        this.derivedRates = derivedRates;
        this.rateCache = rateCache;
    }

    public RateDto calculateRate(String rateName) {
        for(String rawRate : rawRates) {
            if(rawRate.equals(rateName)) {
                return calculateRawRate(rateName);
            }
        }
        for(String derivedRate : derivedRates) {
            if(derivedRate.equals(rateName)) {
                return calculateDerivedRate(rateName);
            }
        }
        return null;
    }

    private RateDto calculateRawRate(String symbol) {
        List<RateDto> rawRates = rateCache.getRawRatesBySymbol(symbol);
        double dividingFactor = rawRates.size();
        double bid = 0.0;
        double ask = 0.0;
        for (RateDto rawRate : rawRates) {
            if(rawRate != null) {
                bid += rawRate.getBid();
            }
        }
        bid /= dividingFactor;
        for (RateDto rawRate : rawRates) {
            if(rawRate != null) {
                ask += rawRate.getAsk();
            }
        }
        ask /= dividingFactor;
        return new RateDto(symbol,bid,ask, rawRates.getFirst().getTimestamp());
    }

    private RateDto calculateDerivedRate(String rateName) {
        List<RateDto> firstRawRates = rateCache.getRawRatesBySymbol(rateName.substring(0,2));
        List<RateDto> secondRawRates = rateCache.getRawRatesBySymbol(rateName.substring(3,5));
        double mid = 0.0;
        double askSum = 0.0;
        double bidSum = 0.0;
        double dividingFactor = firstRawRates.size();
        for (RateDto firstRawRate : firstRawRates) {
            askSum += firstRawRate.getAsk();
        }
        askSum /= dividingFactor;
        for (RateDto firstRawRate : firstRawRates) {
            bidSum += firstRawRate.getBid();
        }
        bidSum /= dividingFactor;
        mid = (askSum + bidSum) / 2.0;

        double ask = 0.0;
        for (RateDto secondRawRate : secondRawRates) {
            ask += secondRawRate.getAsk();
        }
        ask = mid * (ask / dividingFactor);

        double bid = 0.0;
        for (RateDto secondRawRate : secondRawRates) {
            bid += secondRawRate.getBid();
        }
        bid = mid * (bid / dividingFactor);
        return new RateDto(rateName,bid,ask,firstRawRates.getFirst().getTimestamp());
    }

}

