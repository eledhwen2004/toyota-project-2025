package com.main.Mapper;

import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;

import java.time.Instant;

/**
 * The `RateMapper` class provides utility methods to convert between different representations of rate data,
 * specifically between `RateDto` and `RateEntity`, as well as converting `RateDto` to a serialized string format
 * and back. It facilitates mapping between the data transfer object (DTO), entity, and string formats for Kafka
 * serialization and database persistence.
 */
public class RateMapper {

    /**
     * Converts a serialized string representation of a `RateDto` into an actual `RateDto` object.
     * The string is expected to be in the format "rateName|bid|ask|timestamp".
     *
     * @param rateDtoAsString The string representation of the `RateDto`.
     * @return A `RateDto` object corresponding to the parsed string.
     */
    public static RateDto stringToRateDto(String rateDtoAsString) {
        String[] fields = rateDtoAsString.split("\\|");
        String rateName = fields[0];
        double bid = Double.parseDouble(fields[1]);
        double ask = Double.parseDouble(fields[2]);
        Instant timestamp = Instant.parse(fields[3]);
        return new RateDto(rateName, bid, ask, timestamp);
    }

    /**
     * Converts a `RateDto` object into its string representation, where the fields are separated by "|".
     * The format is "rateName|bid|ask|timestamp".
     *
     * @param rateDto The `RateDto` object to convert.
     * @return A string representation of the `RateDto` object.
     */
    public static String rateDtoToString(RateDto rateDto) {
        String rateName = rateDto.getRateName();
        String bid = Double.toString(rateDto.getBid());
        String ask = Double.toString(rateDto.getAsk());
        String timestamp = rateDto.getTimestamp().toString();
        return rateName + "|" + bid + "|" + ask + "|" + timestamp;
    }

    /**
     * Converts a `RateDto` object to a `RateEntity` object for database persistence.
     * The `RateEntity` is an entity class that is typically mapped to a database table.
     *
     * @param rateDto The `RateDto` object to convert.
     * @return A `RateEntity` object corresponding to the `RateDto`.
     */
    public static RateEntity rateDtoToRateEntity(RateDto rateDto) {
        RateEntity rateEntity = new RateEntity();
        rateEntity.rateName = rateDto.getRateName();
        rateEntity.ask = rateDto.getAsk();
        rateEntity.bid = rateDto.getBid();
        rateEntity.rateUpdateTime = rateDto.getTimestamp();
        return rateEntity;
    }

    /**
     * Converts a `RateEntity` object to a `RateDto` object for transferring data between layers.
     *
     * @param rateEntity The `RateEntity` object to convert.
     * @return A `RateDto` object corresponding to the `RateEntity`.
     */
    public static RateDto rateEntityToRateDto(RateEntity rateEntity) {
        RateDto rateDto = new RateDto();
        rateDto.setRateName(rateEntity.rateName);
        rateDto.setAsk(rateEntity.ask);
        rateDto.setBid(rateEntity.bid);
        rateDto.setTimestamp(rateEntity.rateUpdateTime);
        return rateDto;
    }
}
