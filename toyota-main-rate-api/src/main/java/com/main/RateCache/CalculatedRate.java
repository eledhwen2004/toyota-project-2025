package com.main.RateCache;

import java.time.Instant;

public class CalculatedRate {
    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;

    public CalculatedRate(String rateName,double bid,double ask,Instant timestamp) {
        this.rateName = rateName;
        this.bid = bid;
        this.ask = ask;
        this.timestamp = timestamp;
    }
}
