package com.main.Rate;

import com.main.Configuration.Configuration;
import com.main.Server.Server;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public class Rate extends Thread {
    private Server server;
    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;

    private double rateFrequency;

    public Rate(Server server,String rateName, double bid, double ask) throws IOException {
        this.server = server;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
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
                ask += ask * randRatePercentage;
                bid += bid * randRatePercentage;
            }else{
                ask -= ask * randRatePercentage;
                bid -= bid * randRatePercentage;
            }
            try {
                sleep((long) (rateFrequency*1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            timestamp = Instant.now();
        }while(server.getServerStatus());
    }


}
