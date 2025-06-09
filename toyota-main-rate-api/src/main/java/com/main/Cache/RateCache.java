package com.main.Cache;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.main.Dto.RateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Cache service that handles temporary storage and retrieval of rate data using Hazelcast.
 * <p>
 * This class supports both raw and calculated rate caching through Hazelcast distributed maps.
 */
public class RateCache {

    private final Logger logger = LogManager.getLogger("CacheLogger");
    private final IMap<String, RateDto> rawRateCache;
    private final IMap<String, RateDto> calculatedRateCache;
    private final HazelcastInstance hazelcastInstance;

    /**
     * Initializes the Hazelcast instance and retrieves references to the raw and calculated rate caches.
     */
    public RateCache() {
        logger.info("Rate Cache is being initialized");
        this.hazelcastInstance = Hazelcast.getHazelcastInstanceByName("hazelcast-instance");
        this.rawRateCache = hazelcastInstance.getMap("raw-rate-cache");
        this.calculatedRateCache = hazelcastInstance.getMap("calculated-rate-cache");
        logger.info("Rate Cache has been created!");
    }

    /**
     * Retrieves and removes all raw rates that contain the given symbol from the raw rate cache.
     *
     * @param symbol the substring to look for in rate names
     * @return a list of matching {@link RateDto} objects or {@code null} if none found
     */
    public List<RateDto> getRawRatesIfContains(String symbol) {
        logger.info("Rates with \"{}\" symbol has been requested from raw rate cache", symbol);
        List<RateDto> rawRatesBySymbol = new ArrayList<>();
        Collection<RateDto> rawRateList = rawRateCache.values();
        for (RateDto rawRate : rawRateList) {
            if (rawRate.getRateName().contains(symbol)) {
                rawRatesBySymbol.add(rawRate);
                rawRateCache.remove(rawRate.getRateName());
            }
        }
        if (rawRatesBySymbol.isEmpty()) {
            logger.info("There is no raw rates with the symbol \"{}\" in raw rate cache!", symbol);
            return null;
        }
        logger.info("Rate with {} symbol fetched from raw rate cache!", symbol);
        return rawRatesBySymbol;
    }

    /**
     * Retrieves and removes a raw rate by its exact name from the raw rate cache.
     *
     * @param rateName the exact name of the rate
     * @return the {@link RateDto} object or {@code null} if not found
     */
    public RateDto getRawRateByAllName(String rateName) {
        logger.info("{} rate requested!", rateName);
        RateDto rateDto = rawRateCache.remove(rateName);
        if (rateDto == null) {
            logger.info("There is no rate with name \"{}\" in raw rate cache!", rateName);
            return null;
        }
        logger.info("{} rate has been retrieved from raw rate cache", rateName);
        return rateDto;
    }

    /**
     * Retrieves and removes a calculated rate by its name from the calculated rate cache.
     *
     * @param rateName the name of the calculated rate
     * @return the {@link RateDto} object or {@code null} if not found
     */
    public RateDto getCalculatedRateByName(String rateName) {
        logger.info("Calculated {} rate requested!", rateName);
        RateDto rateDto = calculatedRateCache.remove(rateName);
        if (rateDto == null) {
            logger.info("There is no rate with name \"{}\" in calculated rate cache!", rateName);
            return null;
        }
        logger.info("Calculated {} rate has been from retrieved from calculated rate cache", rateName);
        return rateDto;
    }

    /**
     * Inserts or updates a raw rate in the raw rate cache.
     *
     * @param rawRate the {@link RateDto} to be cached
     */
    public void updateRawRate(RateDto rawRate) {
        String rawRateName = rawRate.getRateName();
        logger.info("Raw rate {} is getting updated!", rawRateName);
        rawRateCache.put(rawRateName, rawRate);
        logger.info("Raw rate {} has been updated!", rawRateName);
    }

    /**
     * Inserts or updates a calculated rate in the calculated rate cache.
     *
     * @param calculatedRate the {@link RateDto} to be cached
     */
    public void updateCalculatedRate(RateDto calculatedRate) {
        if (calculatedRate == null) {
            return;
        }
        String calculatedRateName = calculatedRate.getRateName();
        logger.info("Calculated rate {} is getting updated!", calculatedRateName);
        calculatedRateCache.put(calculatedRateName, calculatedRate);
        logger.info("Calculated rate {} has been updated!", calculatedRateName);
    }

    /**
     * Shuts down the Hazelcast instance, cleaning up all distributed resources.
     */
    public void close() {
        this.hazelcastInstance.shutdown();
    }
}
