package com.rate_api.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Data Transfer Object representing a rate with bid, ask, and timestamp information.
 */
@Getter
@Setter
public class RateDto {

    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;

}
