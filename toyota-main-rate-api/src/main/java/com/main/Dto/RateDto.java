package com.main.Dto;

import com.main.Subscriber.RateStatus;

import java.time.Instant;

public class RateDto {
    private String rateName;
    private double ask;
    private double bid;
    private Instant timestamp;
    private RateStatus status;

    public RateDto(String rateName, double ask, double bid, Instant timestamp) {
        this.rateName = rateName;
        this.ask = ask;
        this.bid = bid;
        this.timestamp = timestamp;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
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
