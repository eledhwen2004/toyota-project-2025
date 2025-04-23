package com.main.RateCalculator

import com.main.Dto.RateDto

class DerivedRateCalculationScript{
    def calculate(double []firstRateBids,double []firstRateAsks,double []secondRateBids,double []secondRateAsks){
        double mid = 0.0;
        double askSum = 0.0;
        double bidSum = 0.0;
        double dividingFactor;
        if(firstRateBids.size() == secondRateBids.size() == firstRateAsks.size() == secondRateBids.size){
            dividingFactor = firstRateAsksSize;
        }else{
            return null;
        }
        for (double firstRateAsk : firstRateAsks) {
            askSum += firstRateAsk;
        }
        askSum /= dividingFactor;
        for (double firstRateBid : firstRateBids) {
            bidSum += firstRateBid;
        }
        bidSum /= dividingFactor;
        mid = (askSum + bidSum) / 2.0;

        double derivedRateAsk = 0.0;
        for (double secondRateAsk : secondRateAsks) {
            derivedRateAsk += secondRateAsk;
        }
        derivedRateAsk = mid * (derivedRateAsk / dividingFactor);

        double derivedRateBid = 0.0;
        for (double secondRateBid : secondRateBids) {
            derivedRateBid += secondRateBids;
        }
        derivedRateBid = mid * (derivedRateBid / dividingFactor);
        double [] derivedRateFields = new double[2]();
        derivedRateFields[0] = derivedRateBid;
        derivedRateFields[1] =  derivedRateAsk;
        return derivedRateFields;
    }
}

