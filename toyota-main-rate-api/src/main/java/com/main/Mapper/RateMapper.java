package com.main.Mapper;

import com.main.Dto.RateDto;

import java.time.Instant;

/**
 * Utility class for converting between {@link RateDto} objects and their string representations.
 * <p>
 * The string format is pipe-delimited: {@code rateName|bid|ask|timestamp}
 */
public class RateMapper {

    /**
     * Converts a pipe-delimited string into a {@link RateDto} object.
     * <p>
     * Expected format: {@code rateName|bid|ask|timestamp}
     *
     * @param rateDtoAsString the raw string to parse
     * @return the parsed {@link RateDto} object
     * @throws ArrayIndexOutOfBoundsException if input does not contain exactly 4 fields
     * @throws NumberFormatException if bid or ask cannot be parsed as double
     * @throws java.time.format.DateTimeParseException if timestamp is not ISO-8601 format
     */
    public static RateDto stringToRateDto(String rateDtoAsString) {
        String[] fields = rateDtoAsString.split("\\|");
        String rateName = fields[0];
        double bid = Double.parseDouble(fields[1]);
        double ask = Double.parseDouble(fields[2]);
        Instant timestamp = Instant.parse(fields[3]);
        return new RateDto(rateName, ask, bid, timestamp); // NOT: ask ve bid yer değiştirmişti
    }

    /**
     * Converts a {@link RateDto} object to a pipe-delimited string.
     * <p>
     * Output format: {@code rateName|bid|ask|timestamp}
     *
     * @param rateDto the object to convert
     * @return the string representation of the rate
     */
    public static String rateDtoToString(RateDto rateDto) {
        String rateName = rateDto.getRateName();
        String bid = Double.toString(rateDto.getBid());
        String ask = Double.toString(rateDto.getAsk());
        String timestamp = rateDto.getTimestamp().toString();
        return rateName + "|" + bid + "|" + ask + "|" + timestamp;
    }
}
