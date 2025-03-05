package com.rate_api.Entity;

import com.rate_api.Configuration.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public class Rate extends Thread {
    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;
    private boolean isActive = true;
    private double rateFrequency;

    public Rate(String rateName, double bid, double ask) throws IOException {
        this.setRateName(rateName);
        this.setBid(bid);
        this.setAsk(ask);
        this.setRateFrequency(Configuration.getRateFrequency());
    }

    public String getRateName() {
        return rateName;
    }

    private void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public double getBid() {
        return bid;
    }

    private void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    private void setAsk(double ask) {
        this.ask = ask;
    }

    public void stopRate() {
        isActive = false;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getRateFrequency() {
        return rateFrequency;
    }

    public void setRateFrequency(double rateFrequency) {
        this.rateFrequency = rateFrequency;
    }

    @Override
    public void run() {
        Random rand = new Random();
        Double randRatePercentage;
        Boolean increaseOrDecrease;
        do{
            do {
                randRatePercentage = rand.nextDouble();
            }while(randRatePercentage > 0.011);
            increaseOrDecrease = rand.nextBoolean();
            if(increaseOrDecrease){
                setAsk(ask + (ask * randRatePercentage));
                setBid(bid + (bid * randRatePercentage));
            }else{
                setAsk(ask - (ask * randRatePercentage));
                setBid(bid - (bid * randRatePercentage));
            }
            try {
                sleep((long) (rateFrequency*1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setTimestamp(Instant.now());
        }while(isActive);
    }


}
