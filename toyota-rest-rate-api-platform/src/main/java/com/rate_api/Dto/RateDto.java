package com.rate_api.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing rate information.
 * <p>
 * This class is used to transfer exchange rate data between layers or systems.
 */
@Getter
@Setter
public class RateDto {

    /**
     * The name of the rate (e.g., USDTRY).
     */
    private String rateName;

    /**
     * The bid price of the rate.
     */
    private double bid;

    /**
     * The ask price of the rate.
     */
    private double ask;

    /**
     * The timestamp indicating when the rate was generated or updated.
     */
    private Instant timestamp;

}
