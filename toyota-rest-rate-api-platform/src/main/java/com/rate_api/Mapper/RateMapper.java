package com.rate_api.Mapper;

import com.rate_api.Dto.RateDto;
import com.rate_api.Entity.Rate;

public class RateMapper {

    public static RateDto entityToDto(Rate rateEntity){
        RateDto rateDto = new RateDto();
        rateDto.setRateName(rateEntity.getRateName());
        rateDto.setAsk(rateEntity.getAsk());
        rateDto.setBid(rateEntity.getBid());
        rateDto.setTimestamp(rateEntity.getTimestamp());
        return rateDto;
    }

}
