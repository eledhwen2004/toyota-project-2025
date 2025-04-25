package com.rate_api.Service;

import com.rate_api.Dto.RateDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public interface RateService {
    RateDto getRateByName(String rateName);
}
