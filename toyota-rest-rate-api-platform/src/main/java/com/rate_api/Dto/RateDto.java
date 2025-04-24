package com.rate_api.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RateDto {

    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;

}
