package com.rate_api.Service.Impl;

import com.rate_api.Dto.RateDto;
import com.rate_api.Entity.Rate;
import com.rate_api.Mapper.RateMapper;
import com.rate_api.RateList.RateList;
import com.rate_api.Service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link RateService} interface that provides rate-related services.
 * This service retrieves rate information from a {@link RateList} and converts it to a {@link RateDto}.
 */
@Service
public class RateServiceImpl implements RateService {

    /**
     * The {@link RateList} instance that holds the active rates.
     */
    @Autowired
    private RateList rateList;

    /**
     * Retrieves the rate information for a given rate name.
     * Converts the {@link Rate} entity to a {@link RateDto} before returning it.
     *
     * @param rateName the name of the rate to retrieve.
     * @return a {@link RateDto} containing the rate information.
     */
    @Override
    public RateDto getRateByName(String rateName) {
        Rate rateEntity = rateList.getRate(rateName);
        RateDto rateDto = RateMapper.entityToDto(rateEntity);
        return rateDto;
    }
}
