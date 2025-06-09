package com.rate_api.Mapper;

import com.rate_api.Dto.RateDto;
import com.rate_api.Entity.Rate;

/**
 * Utility class for mapping between {@link Rate} entity and {@link RateDto} DTO.
 * <p>
 * This mapper is primarily used to convert backend Rate thread entities to a
 * transfer object that can be exposed through RESTful endpoints.
 */
public class RateMapper {

    /**
     * Converts a {@link Rate} entity into a {@link RateDto}.
     *
     * @param rateEntity the rate entity to convert
     * @return a {@code RateDto} representing the same data as the input entity
     */
    public static RateDto entityToDto(Rate rateEntity) {
        RateDto rateDto = new RateDto();
        rateDto.setRateName(rateEntity.getRateName());
        rateDto.setAsk(rateEntity.getAsk());
        rateDto.setBid(rateEntity.getBid());
        rateDto.setTimestamp(rateEntity.getTimestamp());
        return rateDto;
    }

}
