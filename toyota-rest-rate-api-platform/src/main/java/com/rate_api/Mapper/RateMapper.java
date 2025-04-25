package com.rate_api.Mapper;

import com.rate_api.Dto.RateDto;
import com.rate_api.Entity.Rate;

/**
 * Utility class for mapping between {@link Rate} entities and {@link RateDto} data transfer objects.
 */
public class RateMapper {

    /**
     * Converts a {@link Rate} entity to a {@link RateDto} object.
     *
     * @param rateEntity the {@link Rate} entity to be converted.
     * @return a {@link RateDto} object containing the data from the provided {@link Rate} entity.
     */
    public static RateDto entityToDto(Rate rateEntity){
        RateDto rateDto = new RateDto();
        rateDto.setRateName(rateEntity.getRateName());
        rateDto.setAsk(rateEntity.getAsk());
        rateDto.setBid(rateEntity.getBid());
        rateDto.setTimestamp(rateEntity.getTimestamp());
        return rateDto;
    }

}
