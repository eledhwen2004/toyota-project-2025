package com.main.Dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing a financial rate, such as bid and ask prices,
 * along with associated metadata like the rate name, timestamp, and status.
 * This class is used for transferring rate-related information across different layers of the application.
 */
@Getter
@Setter
public class RateDto implements Serializable {

    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;
    /**
     * The status of the rate (e.g., available, not available).
     */
    private RateStatus status;
    public RateDto() {}

    /**
     * Constructor for creating a RateDto object with the specified values.
     *
     * @param rateName the name of the rate.
     * @param ask the ask price of the rate.
     * @param bid the bid price of the rate.
     * @param timestamp the timestamp when the rate was recorded.
     */
    public RateDto(String rateName, double ask, double bid, Instant timestamp) {
        this.setRateName(rateName);
        this.setAsk(ask);
        this.setBid(bid);
        this.setTimestamp(timestamp);
        this.setStatus(RateStatus.NOT_AVAILABLE);  // Default status is 'NOT_AVAILABLE'
    }
}
