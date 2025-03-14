package com.main.RateCalculator;

import com.main.Dto.RateDto;
import com.main.Cache.RateCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class RateCalculator {
    private RateCache rateCache;
    private String [] rawRates;
    private String [] derivedRates;
    private final Logger logger = LogManager.getLogger("CalculatorLogger");


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

    private RateDto calculateRawRate(String rateName) {
        logger.info("Calculating Raw Rate for {}", rateName);
        List<RateDto> rawRates = rateCache.getRawRatesBySymbol(rateName);
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
        logger.info("Raw Rate Calculated for {}", rateName);
        return new RateDto(rateName,bid,ask, rawRates.getFirst().getTimestamp());
    }

    private RateDto calculateDerivedRate(String rateName) {
        logger.info("Calculating Derived Rate for  {}",rateName);
        List<RateDto> firstRawRates = rateCache.getRawRatesBySymbol(rateName.substring(0,3));
        List<RateDto> secondRawRates = rateCache.getRawRatesBySymbol(rateName.substring(3,6));
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
        logger.info("Derived Rate Calculated for {}", rateName);
        return new RateDto(rateName,bid,ask,firstRawRates.getFirst().getTimestamp());
    }

}

