package com.rate_api.RateList;

import com.rate_api.Configuration.Configuration;
import com.rate_api.Entity.Rate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RateList implements RateListInterface{
    private List<Rate> rateList;

    public RateList() throws IOException {
        this.rateList = new ArrayList<>();
        String []rateNames = Configuration.getRateNames();
        for(String rateName : rateNames){
            this.addRate(rateName);
            this.getRate(rateName).start();
        }
    }

    @Override
    public void addRate(String rateName) throws IOException {
        double rateAsk = Configuration.getFirstRateAsk(rateName);
        double rateBid = Configuration.getFirstRateBid(rateName);
        rateList.add(new Rate(rateName, rateAsk, rateBid));
    }

    @Override
    public void removeRate(String rateName) {
        for(Rate rate : rateList){
            if(rate.getRateName().equals(rateName)){
                rate.stopRate();
                rateList.remove(rate);
            }
        }
    }

    @Override
    public Rate getRate(String rateName) {
        for(Rate rate : rateList){
            if(rate.getRateName().equals(rateName)){
                return rate;
            }
        }
        return null;
    }
}
