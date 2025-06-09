package com.main.Dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing a currency rate.
 * <p>
 * This class encapsulates rate name, bid/ask values, timestamp of the rate, and its current status.
 * It is used across components (e.g. cache, Kafka, calculation, subscribers) for standardized rate handling.
 */
@Getter
@Setter
public class RateDto implements Serializable {

    /**
     * The name of the rate (e.g., "USD/TRY").
     */
    private String rateName;

    /**
     * The bid price (selling price of base currency).
     */
    private double bid;

    /**
     * The ask price (buying price of base currency).
     */
    private double ask;

    /**
     * Timestamp indicating when the rate was generated or received.
     */
    private Instant timestamp;

    /**
     * Current status of the rate (e.g., NOT_AVAILABLE, AVAILABLE, UPDATED).
     */
    private RateStatus status;

    /**
     * Default constructor for serialization/deserialization.
     */
    public RateDto() {}

    /**
     * Constructs a {@code RateDto} with the given values and sets initial status to {@code NOT_AVAILABLE}.
     *
     * @param rateName  the name of the rate (e.g., "EUR/USD")
     * @param ask       ask price
     * @param bid       bid price
     * @param timestamp the timestamp when the rate is generated
     */
    public RateDto(String rateName, double ask, double bid, Instant timestamp) {
        this.setRateName(rateName);
        this.setAsk(ask);
        this.setBid(bid);
        this.setTimestamp(timestamp);
        this.setStatus(RateStatus.NOT_AVAILABLE);
    }
}
