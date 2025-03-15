package com.main.Mapper;

import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;

import java.time.Instant;

public class RateMapper {

    public static RateDto stringToRateDto(String response){
        String [] fields = response.split("\\|");
        String rateName = fields[0];
        double ask = Double.parseDouble(fields[1]);
        double bid = Double.parseDouble(fields[2]);
        Instant timestamp = Instant.parse(fields[3]);
        return new RateDto(rateName,ask,bid,timestamp);
    }

    public static RateEntity rateDtoToRateEntity(RateDto rateDto){
        RateEntity rateEntity = new RateEntity();
        rateEntity.rateName = rateDto.getRateName();
        rateEntity.ask = rateDto.getAsk();
        rateEntity.bid = rateDto.getBid();
        rateEntity.rateUpdateTime = rateDto.getTimestamp();
        return rateEntity;
    }

}
