package com.main.Mapper;

import com.main.entity.RateEntity;

import java.time.Instant;

public class RateMapper {

    public static RateEntity stringToRateEntity(String rateAsString) {
        RateEntity rateEntity = new RateEntity();
        String [] fields = rateAsString.split("\\|");
        rateEntity.rateName = fields[0];
        rateEntity.ask = Double.parseDouble(fields[2]);
        rateEntity.bid = Double.parseDouble(fields[1]);
        rateEntity.rateUpdateTime = Instant.parse(fields[3]);
        return rateEntity;
    }


}
