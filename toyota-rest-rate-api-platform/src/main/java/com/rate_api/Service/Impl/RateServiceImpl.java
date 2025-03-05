package com.rate_api.Service.Impl;

import com.rate_api.Dto.RateDto;
import com.rate_api.Entity.Rate;
import com.rate_api.Mapper.RateMapper;
import com.rate_api.RateList.RateList;
import com.rate_api.Service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateServiceImpl implements RateService {

    @Autowired
    private RateList rateList;

    @Override
    public RateDto getRateByName(String rateName) {
        Rate rateEntity = rateList.getRate(rateName);
        RateDto rateDto = RateMapper.entityToDto(rateEntity);
        return rateDto;
    }
}
