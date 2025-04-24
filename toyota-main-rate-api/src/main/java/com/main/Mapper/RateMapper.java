package com.main.Mapper;

import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;

import java.time.Instant;

public class RateMapper {

    public static RateDto stringToRateDto(String rateDtoAsString) {
        String [] fields = rateDtoAsString.split("\\|");
        String rateName = fields[0];
        double bid = Double.parseDouble(fields[1]);
        double ask = Double.parseDouble(fields[2]);
        Instant timestamp = Instant.parse(fields[3]);
        return new RateDto(rateName,bid,ask,timestamp);
    }

    public static String rateDtoToString(RateDto rateDto){
        String rateName = rateDto.getRateName();
        String bid = Double.toString(rateDto.getBid());
        String ask = Double.toString(rateDto.getAsk());
        String timestamp = rateDto.getTimestamp().toString();
        return rateName + "|" + bid + "|" + ask + "|" + timestamp;
    }

    public static RateEntity rateDtoToRateEntity(RateDto rateDto){
        RateEntity rateEntity = new RateEntity();
        rateEntity.rateName = rateDto.getRateName();
        rateEntity.ask = rateDto.getAsk();
        rateEntity.bid = rateDto.getBid();
        rateEntity.rateUpdateTime = rateDto.getTimestamp();
        return rateEntity;
    }

    public static RateDto rateEntityToRateDto(RateEntity rateEntity){
        RateDto rateDto = new RateDto();
        rateDto.setRateName(rateEntity.rateName);
        rateDto.setAsk(rateEntity.ask);
        rateDto.setBid(rateEntity.bid);
        rateDto.setTimestamp(rateEntity.rateUpdateTime);
        return rateDto;
    }

}
