package com.main.Dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class RateDto implements Serializable {
    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;
    private RateStatus status;

    public RateDto(){}

    public RateDto(String rateName, double ask, double bid, Instant timestamp) {
        this.setRateName(rateName);
        this.setAsk(ask);
        this.setBid(bid);
        this.setTimestamp(timestamp);
        this.setStatus(RateStatus.NOT_AVAILABLE);
    }

}
