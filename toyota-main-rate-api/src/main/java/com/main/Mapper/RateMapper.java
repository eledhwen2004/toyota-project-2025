package com.main.Mapper;

import com.main.Dto.RateDto;

import java.time.Instant;

public class RateMapper {

    public static RateDto stringToRateDto(String response){
        String [] fields = response.split("\\|");
        String rateName = fields[0];
        double ask = Double.parseDouble(fields[1]);
        double bid = Double.parseDouble(fields[2]);
        Instant timestamp = Instant.parse(fields[3]);
        return new RateDto(rateName,ask,bid,timestamp);
    }

}
