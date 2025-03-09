package com.main.RateCalculator;

import com.main.RateCache.CalculatedRate;
import com.main.RateCache.RawRate;

public class RateCalculator {

    public static CalculatedRate calculateRawRate(RawRate[]rawRates) {
        String rateName = rawRates[0].getRateName();
        int dividingFactor = rawRates.length;
        double ask = 0.0;
        double bid = 0.0;
        for (RawRate rawRate : rawRates) {
            ask += rawRate.getAsk();
        }
        ask /= dividingFactor;
        for (RawRate rawRate : rawRates) {
            bid += rawRate.getBid();
        }
        return new CalculatedRate(
                rateName,bid,ask,rawRates[0].getTimestamp()
        );
    }

    public static CalculatedRate calculateDerivedRate(RawRate[] firstRawRates, RawRate[] secondRawRates) {
        double mid = 0.0;
        double askSum = 0.0;
        double bidSum = 0.0;
        int dividingFactor = firstRawRates.length;
        for (RawRate firstRawRate : firstRawRates) {
            askSum += firstRawRate.getAsk();
        }
        askSum /= dividingFactor;
        for (RawRate firstRawRate : firstRawRates) {
            bidSum += firstRawRate.getBid();
        }
        bidSum /= dividingFactor;
        mid = (askSum + bidSum) / 2.0;

        String rateName = firstRawRates[0].getRateName().substring(0,2) + secondRawRates[0].getRateName().substring(3);

        double ask = 0.0;
        for (RawRate secondRawRate : secondRawRates) {
            ask += secondRawRate.getAsk();
        }
        ask = mid * (ask / dividingFactor);

        double bid = 0.0;
        for (RawRate secondRawRate : secondRawRates) {
            bid += secondRawRate.getBid();
        }
        bid = mid * (bid / dividingFactor);
        return new CalculatedRate(rateName,bid,ask,firstRawRates[0].getTimestamp());
    }

}
