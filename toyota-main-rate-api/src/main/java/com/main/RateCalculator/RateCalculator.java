package com.main.RateCalculator;

import com.main.Cache.RateCache;
import com.main.Dto.RateDto;
import groovy.lang.GroovyClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Service class responsible for calculating both raw and derived exchange rates.
 * <p>
 * Groovy scripts are dynamically loaded at runtime to perform flexible rate calculations.
 */
public class RateCalculator {

    private final RateCache rateCache;
    private final String[] rawRateNames;
    private final String[] derivedRates;
    private final Logger logger = LogManager.getLogger("CalculatorLogger");

    /**
     * Name of the raw rate calculation Groovy script resource.
     */
    public final String rawRateCalculationScriptUrl = "RawRateCalculationScript.groovy";

    /**
     * Name of the derived rate calculation Groovy script resource.
     */
    public final String derivedRateCalculationScriptUrl = "DerivedRateCalculationScript.groovy";

    /**
     * Constructs the calculator with necessary rate configuration and caching support.
     *
     * @param rateCache     Hazelcast-backed rate cache
     * @param rawRates      array of raw rate names (e.g. USDTRY)
     * @param derivedRates  array of derived rate names (e.g. EURTRY via EURUSD & USDTRY)
     */
    public RateCalculator(RateCache rateCache, String[] rawRates, String[] derivedRates) {
        this.rateCache = rateCache;
        this.rawRateNames = rawRates;
        this.derivedRates = derivedRates;
        logger.info("RateCalculator initialized with {} raw rates and {} derived rates", rawRates.length, derivedRates.length);
    }

    /**
     * Executes the raw rate Groovy script and returns calculated ask-bid array.
     *
     * @param asks array of ask prices
     * @param bids array of bid prices
     * @return array containing calculated ask and bid
     */
    public double[] rawRateCalculationMethod(double[] asks, double[] bids) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(rawRateCalculationScriptUrl)) {
            if (inputStream == null)
                throw new FileNotFoundException("Raw Groovy script not found in classpath: " + rawRateCalculationScriptUrl);

            File tempScript = File.createTempFile("RawRateCalculationScript", ".groovy");
            try (FileOutputStream out = new FileOutputStream(tempScript)) {
                inputStream.transferTo(out);
            }

            Class<?> groovyClass = groovyClassLoader.parseClass(tempScript);
            Object groovyObject = groovyClass.getDeclaredConstructor().newInstance();
            Method method = groovyClass.getMethod("calculate", double[].class, double[].class);

            return (double[]) method.invoke(groovyObject, asks, bids);

        } catch (Exception e) {
            logger.error("Error in rawRateCalculationMethod", e);
        }
        return null;
    }

    /**
     * Executes the derived rate Groovy script using two base rates and returns calculated ask-bid array.
     *
     * @param firstRateBids   bid values of first base rate
     * @param firstRateAsks   ask values of first base rate
     * @param secondRateBids  bid values of second base rate
     * @param secondRateAsks  ask values of second base rate
     * @return array containing calculated ask and bid
     */
    public double[] derivedRateCalculationMethod(double[] firstRateBids, double[] firstRateAsks,
                                                 double[] secondRateBids, double[] secondRateAsks) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(derivedRateCalculationScriptUrl)) {
            if (inputStream == null)
                throw new FileNotFoundException("Derived Groovy script not found in classpath: " + derivedRateCalculationScriptUrl);

            File tempScript = File.createTempFile("DerivedRateCalculationScript", ".groovy");
            try (FileOutputStream out = new FileOutputStream(tempScript)) {
                inputStream.transferTo(out);
            }

            Class<?> groovyClass = groovyClassLoader.parseClass(tempScript);
            Object groovyObject = groovyClass.getDeclaredConstructor().newInstance();
            Method method = groovyClass.getMethod("calculate",
                    double[].class, double[].class, double[].class, double[].class);

            return (double[]) method.invoke(groovyObject,
                    firstRateBids, firstRateAsks, secondRateBids, secondRateAsks);

        } catch (Exception e) {
            logger.error("Error in derivedRateCalculationMethod", e);
        }
        return null;
    }

    /**
     * Calculates the given rate based on whether it is raw or derived.
     *
     * @param rateName the name of the rate to calculate
     * @return a populated {@link RateDto}, or null if calculation fails
     */
    public RateDto calculateRate(String rateName) {
        logger.info("Calculating rate for: {}", rateName);
        for (String rawRateName : rawRateNames) {
            if (rawRateName.equals(rateName)) {
                return calculateRawRate(rateName);
            }
        }
        for (String derivedRate : derivedRates) {
            if (derivedRate.equals(rateName)) {
                return calculateDerivedRate(rateName);
            }
        }
        logger.warn("Requested rate {} not found in raw or derived rate lists", rateName);
        return null;
    }

    /**
     * Calculates a raw rate by averaging multiple values from the cache.
     *
     * @param rateName the raw rate name
     * @return calculated {@link RateDto} or null if insufficient data
     */
    private RateDto calculateRawRate(String rateName) {
        logger.info("Calculating Raw Rate for {}", rateName);
        List<RateDto> rawRateList = rateCache.getRawRatesIfContains(rateName);
        if (rawRateList == null || rawRateList.isEmpty()) {
            logger.error("Raw rate data missing or null for derived rate calculation: {}", rateName);
            return null;
        }

        double[] bids = rawRateList.stream().mapToDouble(RateDto::getBid).toArray();
        double[] asks = rawRateList.stream().mapToDouble(RateDto::getAsk).toArray();

        double[] rateFields = rawRateCalculationMethod(asks, bids);
        if (rateFields == null || rateFields.length < 2) {
            logger.error("Raw rate calculation returned null or invalid result for {}", rateName);
            return null;
        }

        logger.info("Raw Rate Calculated for {}", rateName);
        return new RateDto(rateName, rateFields[0], rateFields[1], rawRateList.getFirst().getTimestamp());
    }

    /**
     * Calculates a derived rate based on two underlying raw rates.
     *
     * @param rateName the derived rate name (e.g. "EURTRY")
     * @return calculated {@link RateDto} or null if inputs are missing or invalid
     */
    private RateDto calculateDerivedRate(String rateName) {
        logger.info("Calculating Derived Rate for {}", rateName);
        List<RateDto> firstRawRateList = rateCache.getRawRatesIfContains(rateName.substring(0, 3));
        List<RateDto> secondRawRateList = rateCache.getRawRatesIfContains(rateName.substring(3, 6));

        if (firstRawRateList == null || secondRawRateList == null ||
                firstRawRateList.isEmpty() || secondRawRateList.isEmpty()) {
            logger.error("Raw rate data missing or null for derived rate calculation: {}", rateName);
            return null;
        }

        double[] firstRateBids = firstRawRateList.stream().mapToDouble(RateDto::getBid).toArray();
        double[] firstRateAsks = firstRawRateList.stream().mapToDouble(RateDto::getAsk).toArray();
        double[] secondRateBids = secondRawRateList.stream().mapToDouble(RateDto::getBid).toArray();
        double[] secondRateAsks = secondRawRateList.stream().mapToDouble(RateDto::getAsk).toArray();

        double[] rateFields = derivedRateCalculationMethod(firstRateBids, firstRateAsks, secondRateBids, secondRateAsks);
        if (rateFields == null || rateFields.length < 2) {
            logger.error("Derived rate calculation failed or returned invalid result for {}", rateName);
            return null;
        }

        logger.info("Derived Rate Calculated for {}", rateName);
        return new RateDto(rateName, rateFields[0], rateFields[1], secondRawRateList.getFirst().getTimestamp());
    }
}
