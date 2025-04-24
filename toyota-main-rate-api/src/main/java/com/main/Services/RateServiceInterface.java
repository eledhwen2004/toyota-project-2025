package com.main.Services;


import com.main.Dto.RateDto;

import java.util.List;


public interface RateServiceInterface {
    RateDto getRawRateByAllName(String rateName);
    List<RateDto> getRawRatesIfContains(String symbol);
    RateDto getCalculatedRateByName(String rateName);
}
