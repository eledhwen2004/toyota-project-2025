package com.main.Dto;

import com.main.Subscriber.RateStatus;

import java.time.Instant;

public class RateDto {
    private String rateName;
    private String platformName;
    private double ask;
    private double bid;
    private Instant timestamp;
    private RateStatus status;

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public RateStatus getStatus() {
        return status;
    }

    public void setStatus(RateStatus status) {
        this.status = status;
    }


}
