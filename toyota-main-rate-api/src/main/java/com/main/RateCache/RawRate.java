package com.main.RateCache;

import com.main.Dto.RateDto;

import java.time.Instant;

public class RawRate {
    private String platformName;
    private String rateName;
    private String firstCurrency;
    private String secondCurrency;
    private double bid;
    private double ask;
    private Instant timestamp;

    public String getPlatformName() {
        return platformName;
    }

    public String getRateName() {
        return rateName;
    }

    public String getFirstCurrency() {
        return firstCurrency;
    }

    public String getSecondCurrency() {
        return secondCurrency;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    public Instant getTimestamp() {
        return timestamp;
    }


    public RawRate(RateDto rate){
        this.platformName = rate.getRateName().split("_")[0];
        this.rateName = rate.getRateName().split("_")[1];
        this.firstCurrency = rateName.substring(0,2);
        this.secondCurrency = rateName.substring(2);
        this.bid = rate.getBid();
        this.ask = rate.getAsk();
        this.timestamp = rate.getTimestamp();
    }
}
