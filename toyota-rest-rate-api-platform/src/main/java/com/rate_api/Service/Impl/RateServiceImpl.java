package com.rate_api.Service.Impl;

import com.rate_api.Dto.RateDto;
import com.rate_api.Entity.Rate;
import com.rate_api.Mapper.RateMapper;
import com.rate_api.RateList.RateList;
import com.rate_api.Service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link RateService} interface.
 * <p>
 * This service handles business logic related to exchange rates.
 * It fetches the requested {@link Rate} entity from the {@link RateList},
 * and maps it to a {@link RateDto} object to be used by the API layer.
 * </p>
 */
@Service
public class RateServiceImpl implements RateService {

    /**
     * The list of all active rate entities.
     * Injected by Spring as a singleton bean.
     */
    @Autowired
    private RateList rateList;

    /**
     * Retrieves the current rate information for the given rate name.
     *
     * @param rateName the name of the currency rate to fetch (e.g., "USDTRY")
     * @return a {@link RateDto} containing the current ask, bid, and timestamp
     */
    @Override
    public RateDto getRateByName(String rateName) {
        Rate rateEntity = rateList.getRate(rateName);
        RateDto rateDto = RateMapper.entityToDto(rateEntity);
        return rateDto;
    }
}
