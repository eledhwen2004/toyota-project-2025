package com.main.Mapper;

import com.main.entity.RateEntity;

import java.time.Instant;

/**
 * Utility class responsible for mapping raw string representations of rate data
 * into {@link RateEntity} objects.
 */
public class RateMapper {

    /**
     * Converts a pipe-delimited string into a {@link RateEntity}.
     * <p>
     * The expected format of the input string is: {@code rateName|bid|ask|rateUpdateTime}
     * <ul>
     *     <li><b>rateName</b> - e.g., "USD/TRY"</li>
     *     <li><b>bid</b> - bid value as a double (e.g., "32.45")</li>
     *     <li><b>ask</b> - ask value as a double (e.g., "32.65")</li>
     *     <li><b>rateUpdateTime</b> - ISO-8601 formatted timestamp (e.g., "2025-06-09T09:30:00Z")</li>
     * </ul>
     *
     * @param rateAsString the pipe-delimited string representing rate data
     * @return the corresponding {@link RateEntity} object
     * @throws ArrayIndexOutOfBoundsException if the input string does not have the expected 4 parts
     * @throws NumberFormatException if bid/ask fields are not valid doubles
     * @throws java.time.format.DateTimeParseException if rateUpdateTime is not a valid ISO-8601 timestamp
     */
    public static RateEntity stringToRateEntity(String rateAsString) {
        RateEntity rateEntity = new RateEntity();
        String[] fields = rateAsString.split("\\|");
        rateEntity.rateName = fields[0];
        rateEntity.ask = Double.parseDouble(fields[2]);
        rateEntity.bid = Double.parseDouble(fields[1]);
        rateEntity.rateUpdateTime = Instant.parse(fields[3]);
        return rateEntity;
    }
}
