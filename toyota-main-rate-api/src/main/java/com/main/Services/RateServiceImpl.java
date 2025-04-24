package com.main.Services;


import com.main.Cache.RateCache;
import com.main.Database.PostgresqlDatabase;
import com.main.Dto.RateDto;

import java.util.ArrayList;
import java.util.List;

public class RateServiceImpl implements RateServiceInterface {

    private final RateCache rateCache;
    private final PostgresqlDatabase database;
    private String [] rawRateNames;
    private String [] calculatedRateNames;

    public RateServiceImpl(RateCache rateCache,
                           PostgresqlDatabase database,
                           String [] rawRateNames,
                           String [] calculatedRateNames) {
        this.rateCache = rateCache;
        this.database = database;
        this.rawRateNames = rawRateNames;
        this.calculatedRateNames = calculatedRateNames;
    }

    @Override
    public RateDto getRawRateByAllName(String rateName) {
        RateDto rateDto = rateCache.getRawRateByAllName(rateName);
        if(rateDto == null) {
            return database.getLatestRateByName(rateName);
        }
        return rateDto;
    }

    @Override
    public List<RateDto> getRawRatesIfContains(String symbol) {
        List<RateDto> rateDtoList = rateCache.getRawRatesIfContains(symbol);
        if(rateDtoList == null) {
            rateDtoList = new ArrayList<>();
            for(String rawRateName : rawRateNames) {
                RateDto rateDto = database.getLatestRateByName(rawRateName);
                if(rateDto != null && rateDto.getRateName().contains(symbol)) {
                    rateDtoList.add(rateDto);
                }
            }
            return rateDtoList;
        }else{
            return rateDtoList;
        }
    }

    @Override
    public RateDto getCalculatedRateByName(String rateName) {
        RateDto rateDto = rateCache.getCalculatedRateByName(rateName);
        if(rateDto == null) {
            return database.getLatestRateByName(rateName);
        }
        return rateDto;
    }

}
