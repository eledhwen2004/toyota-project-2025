package com.rate_api.Entity;

import com.rate_api.Configuration.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

@Getter
@Setter
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

    public void stopRate() {
        isActive = false;
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
