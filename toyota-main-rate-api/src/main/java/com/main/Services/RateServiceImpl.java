package com.main.Services;

import com.main.Cache.RateCache;
import com.main.Database.PostgresqlDatabase;
import com.main.Dto.RateDto;

import java.util.ArrayList;
import java.util.List;

/**
 * The `RateServiceImpl` class implements the `RateServiceInterface` and provides business logic for managing rate data.
 * It integrates with both a cache (RateCache) and a database (PostgresqlDatabase) to retrieve rate information,
 * reducing the need for repeated database queries and enhancing performance.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Fetching raw rates either from the cache or database.</li>
 *     <li>Fetching calculated rates either from the cache or database.</li>
 *     <li>Fetching multiple rates based on a symbol (for partial matches).</li>
 * </ul>
 * </p>
 *
 * @see RateServiceInterface
 * @see RateCache
 * @see PostgresqlDatabase
 */
public class RateServiceImpl implements RateServiceInterface {

    private final RateCache rateCache;
    private final PostgresqlDatabase database;
    private String[] rawRateNames;
    private String[] calculatedRateNames;

    /**
     * Constructs a `RateServiceImpl` instance with the provided rate cache, database, and rate names.
     *
     * @param rateCache The cache where rate data is stored for faster access.
     * @param database The database from which rate data can be fetched if not found in the cache.
     * @param rawRateNames An array of raw rate names used to identify the rate data.
     * @param calculatedRateNames An array of calculated rate names used to identify the calculated rate data.
     */
    public RateServiceImpl(RateCache rateCache,
                           PostgresqlDatabase database,
                           String[] rawRateNames,
                           String[] calculatedRateNames) {
        this.rateCache = rateCache;
        this.database = database;
        this.rawRateNames = rawRateNames;
        this.calculatedRateNames = calculatedRateNames;
    }

    /**
     * Fetches a raw rate by its name from the cache or the database.
     * If the rate is found in the cache, it is returned; otherwise, it is retrieved from the database.
     *
     * @param rateName The name of the rate to retrieve.
     * @return The `RateDto` representing the raw rate, or null if the rate cannot be found.
     */
    @Override
    public RateDto getRawRateByAllName(String rateName) {
        RateDto rateDto = rateCache.getRawRateByAllName(rateName);
        if (rateDto == null) {
            return database.getLatestRateByName(rateName);
        }
        return rateDto;
    }

    /**
     * Fetches a list of raw rates that contain the given symbol in their name.
     * The cache is checked first, and if no results are found, the database is queried.
     *
     * @param symbol The symbol to search for in the rate names.
     * @return A list of `RateDto` objects representing the raw rates containing the symbol.
     */
    @Override
    public List<RateDto> getRawRatesIfContains(String symbol) {
        List<RateDto> rateDtoList = rateCache.getRawRatesIfContains(symbol);
        if (rateDtoList == null) {
            rateDtoList = new ArrayList<>();
            for (String rawRateName : rawRateNames) {
                RateDto rateDto = database.getLatestRateByName(rawRateName);
                if (rateDto != null && rateDto.getRateName().contains(symbol)) {
                    rateDtoList.add(rateDto);
                }
            }
            return rateDtoList;
        }
        return rateDtoList;
    }

    /**
     * Fetches a calculated rate by its name from the cache or the database.
     * If the rate is found in the cache, it is returned; otherwise, it is retrieved from the database.
     *
     * @param rateName The name of the calculated rate to retrieve.
     * @return The `RateDto` representing the calculated rate, or null if the rate cannot be found.
     */
    @Override
    public RateDto getCalculatedRateByName(String rateName) {
        RateDto rateDto = rateCache.getCalculatedRateByName(rateName);
        if (rateDto == null) {
            rateDto = database.getLatestRateByName(rateName);
            if (rateDto != null) {
                return rateDto;
            }
            return null;
        }
        return rateDto;
    }

}
