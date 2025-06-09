package com.rate_api.Service;

import com.rate_api.Dto.RateDto;
import org.springframework.stereotype.Component;

/**
 * Service interface for accessing exchange rate information.
 * <p>
 * Implementations of this interface are responsible for retrieving
 * the current rate data for a given currency pair.
 * </p>
 */
@Component
public interface RateService {

    /**
     * Retrieves the latest {@link RateDto} data for the specified rate name.
     *
     * @param rateName the name of the rate to fetch (e.g., "USDTRY")
     * @return a {@link RateDto} object containing bid, ask, and timestamp values
     */
    RateDto getRateByName(String rateName);
}
