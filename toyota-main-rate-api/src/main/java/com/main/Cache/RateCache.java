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
 * The RateCache class provides an abstraction over Hazelcast to manage
 * raw and calculated rates in the cache. It provides methods to retrieve,
 * update, and manage cached rates by their names or symbols.
 *
 * It utilizes Hazelcast's distributed map implementation to ensure
 * data consistency across distributed systems.
 */
public class RateCache {

    private final Logger cacheLogger = LogManager.getLogger("CacheLogger");
    private final IMap<String, RateDto> rawRateCache;
    private final IMap<String, RateDto> calculatedRateCache;
    private final HazelcastInstance hazelcastInstance;

    /**
     * Constructs a RateCache object and initializes the Hazelcast instance
     * as well as the raw and calculated rate caches.
     */
    public RateCache(){
        cacheLogger.info("Rate Cache is being initialized");
        this.hazelcastInstance = Hazelcast.getHazelcastInstanceByName("hazelcast-instance");
        this.rawRateCache = hazelcastInstance.getMap("raw-rate-cache");
        this.calculatedRateCache = hazelcastInstance.getMap("calculated-rate-cache");
        cacheLogger.info("Rate Cache has been created!");
    }

    /**
     * Retrieves a list of raw rates that contain the specified symbol.
     * If no rates are found with the given symbol, a warning is logged.
     *
     * @param symbol The symbol to search for in raw rates.
     * @return A list of RateDto objects that match the symbol, or null if no matching rates are found.
     */
    public List<RateDto> getRawRatesIfContains(String symbol){
        cacheLogger.info("Rates with \"{}\" symbol has been requested from raw rate cache", symbol);
        List<RateDto> rawRatesBySymbol = new ArrayList<>();
        Collection<RateDto> rawRateList = rawRateCache.values();
        for(RateDto rawRate : rawRateList){
            if(rawRate.getRateName().contains(symbol)){
                rawRatesBySymbol.add(rawRate);
            }
        }
        if(rawRatesBySymbol.isEmpty()){
            cacheLogger.warn("There is no raw rates with the symbol \"{}\" in raw rate cache!", symbol);
            return null;
        }
        cacheLogger.info("Rate with {} symbol fetched from raw rate cache!", symbol);
        return rawRatesBySymbol;
    }

    /**
     * Retrieves a raw rate by its name.
     * If the rate is not found, a warning is logged.
     *
     * @param rateName The name of the rate to retrieve.
     * @return The RateDto object for the requested raw rate, or null if not found.
     */
    public RateDto getRawRateByAllName(String rateName){
        cacheLogger.info("{} rate requested!", rateName);
        RateDto rateDto = rawRateCache.get(rateName);
        if(rateDto == null){
            cacheLogger.warn("There is no rate with name \"{}\" in raw rate cache!", rateName);
            return null;
        }
        cacheLogger.info("{} rate has been retrieved from raw rate cache", rateName);
        return rateDto;
    }

    /**
     * Retrieves a calculated rate by its name.
     * If the rate is not found, a warning is logged.
     *
     * @param rateName The name of the calculated rate to retrieve.
     * @return The RateDto object for the requested calculated rate, or null if not found.
     */
    public RateDto getCalculatedRateByName(String rateName){
        cacheLogger.info("Calculated {} rate requested!", rateName);
        RateDto rateDto = calculatedRateCache.get(rateName);
        if(rateDto == null){
            cacheLogger.warn("There is no rate with name \"{}\" in calculated rate cache!", rateName);
            return null;
        }
        cacheLogger.info("Calculated {} rate has been retrieved from calculated rate cache", rateName);
        return rateDto;
    }

    /**
     * Updates the raw rate cache with the provided raw rate.
     * Logs the update process.
     *
     * @param rawRate The RateDto object containing the raw rate to update.
     */
    public void updateRawRate(RateDto rawRate){
        String rawRateName = rawRate.getRateName();
        cacheLogger.info("Raw rate {} is getting updated!", rawRateName);
        rawRateCache.put(rawRateName, rawRate);
        cacheLogger.info("Raw rate {} has been updated!", rawRateName);
    }

    /**
     * Updates the calculated rate cache with the provided calculated rate.
     * If the provided rate is null, no update is performed.
     * Logs the update process.
     *
     * @param calculatedRate The RateDto object containing the calculated rate to update.
     */
    public void updateCalculatedRate(RateDto calculatedRate){
        if(calculatedRate == null){
            return;
        }
        String calculatedRateName = calculatedRate.getRateName();
        cacheLogger.info("Calculated rate {} is getting updated!", calculatedRateName);
        calculatedRateCache.put(calculatedRateName, calculatedRate);
        cacheLogger.info("Calculated rate {} has been updated!", calculatedRateName);
    }

    /**
     * Shuts down the Hazelcast instance and closes the cache.
     * Logs the shutdown process.
     */
    public void close(){
        cacheLogger.info("Cache is being closed");
        this.hazelcastInstance.shutdown();
        cacheLogger.info("Cache has been closed");
    }
}
