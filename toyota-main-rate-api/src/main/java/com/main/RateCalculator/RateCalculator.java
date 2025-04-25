package com.main.RateCalculator;

import com.main.Dto.RateDto;
import com.main.Services.RateServiceInterface;
import groovy.lang.GroovyClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * The `RateCalculator` class is responsible for calculating rates based on raw and derived data.
 * It interacts with external Groovy scripts for rate calculations and uses a `RateServiceInterface`
 * to fetch raw rate data. The class supports calculating both raw rates and derived rates by using
 * Groovy scripts located in specific paths.
 * <p>
 * The raw rates are calculated directly from bids and asks, while derived rates are computed using
 * two different raw rates, following a specific calculation logic defined in the Groovy scripts.
 * </p>
 */
public class RateCalculator {
    private RateServiceInterface rateService;
    private String[] rawRateNames;
    private String[] derivedRates;
    private final Logger calculatorLogger = LogManager.getLogger("CalculatorLogger");

    public final String rawRatecalculationScriptUrl = System.getProperty("user.dir") + "/toyota-main-rate-api/Scripts/RateCalculation/RawRateCalculationScript.groovy";
    public final String derivedRateCalculationScriptUrl = System.getProperty("user.dir") + "/toyota-main-rate-api/Scripts/RateCalculation/DerivedRateCalculationScript.groovy";

    /**
     * Constructor for initializing the `RateCalculator` with necessary dependencies.
     *
     * @param rateService   The service used to fetch raw rates.
     * @param rawRates      The names of the raw rates.
     * @param derivedRates  The names of the derived rates.
     */
    public RateCalculator(RateServiceInterface rateService, String[] rawRates, String[] derivedRates) {
        this.rawRateNames = rawRates;
        this.derivedRates = derivedRates;
        this.rateService = rateService;
    }

    /**
     * Calculates the raw rates using a Groovy script. It uses the provided asks and bids arrays,
     * invokes the Groovy script, and returns the calculated results.
     *
     * @param asks  The array of ask prices for the raw rate calculation.
     * @param bids  The array of bid prices for the raw rate calculation.
     * @return      An array of calculated rate fields (e.g., rate1, rate2).
     */
    public double[] rawRateCalculationMethod(double[] asks, double[] bids) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try {
            File groovyClassFile = new File(rawRatecalculationScriptUrl);
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyClassFile);
            Object groovyObject = groovyClass.newInstance();
            Method method = groovyClass.getMethod("calculate", double[].class, double[].class);
            return (double[]) method.invoke(groovyObject, asks, bids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calculates the derived rates using a Groovy script. It uses bid and ask arrays from two raw rates
     * to calculate the derived rate fields.
     *
     * @param firstRateBids     The bid prices of the first raw rate.
     * @param firstRateAsks     The ask prices of the first raw rate.
     * @param secondRateBids    The bid prices of the second raw rate.
     * @param secondRateAsks    The ask prices of the second raw rate.
     * @return                  An array of calculated derived rate fields.
     */
    public double[] derivedRateCalculationMethod(double[] firstRateBids, double[] firstRateAsks, double[] secondRateBids, double[] secondRateAsks) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try {
            File groovyClassFile = new File(derivedRateCalculationScriptUrl);
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyClassFile);
            Object groovyObject = groovyClass.newInstance();
            Method method = groovyClass.getMethod("calculate", double[].class, double[].class, double[].class, double[].class);
            return (double[]) method.invoke(groovyObject, firstRateBids, firstRateAsks, secondRateBids, secondRateAsks);
        } catch (Exception e) {
            calculatorLogger.warn(e.getMessage());
        }
        return null;
    }

    /**
     * Calculates the rate for a given rate name. The method determines whether the rate is a raw rate or a derived rate,
     * and calls the corresponding calculation method.
     *
     * @param rateName The name of the rate to be calculated (either raw or derived).
     * @return         The calculated `RateDto` containing the rate details, or null if the rate can't be calculated.
     */
    public RateDto calculateRate(String rateName) {
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
        return null;
    }

    /**
     * Calculates a raw rate for a given rate name. It fetches the corresponding raw rates from the `RateServiceInterface`,
     * invokes the raw rate calculation method, and returns the calculated `RateDto`.
     *
     * @param rateName The name of the raw rate to be calculated.
     * @return         A `RateDto` object containing the calculated raw rate, or null if raw rate data is missing.
     */
    private RateDto calculateRawRate(String rateName) {
        calculatorLogger.info("Calculating Raw Rate for {}", rateName);
        List<RateDto> rawRateList = rateService.getRawRatesIfContains(rateName);
        if (rawRateList.isEmpty()) {
            return null;
        }
        double[] bids = new double[rawRateList.size()];
        double[] asks = new double[rawRateList.size()];
        for (int i = 0; i < rawRateList.size(); i++) {
            bids[i] = rawRateList.get(i).getBid();
        }
        for (int i = 0; i < rawRateList.size(); i++) {
            asks[i] = rawRateList.get(i).getAsk();
        }
        double[] rateFields = (double[]) rawRateCalculationMethod(bids, asks);
        calculatorLogger.info("Raw Rate Calculated for {}", rateName);
        return new RateDto(rateName, rateFields[0], rateFields[1], rawRateList.get(0).getTimestamp());
    }

    /**
     * Calculates a derived rate for a given rate name. It fetches the corresponding raw rates, and invokes the derived rate
     * calculation method. The calculated derived rate is returned as a `RateDto`.
     *
     * @param rateName The name of the derived rate to be calculated.
     * @return         A `RateDto` object containing the calculated derived rate, or null if the necessary raw rates are missing.
     */
    private RateDto calculateDerivedRate(String rateName) {
        calculatorLogger.info("Calculating Derived Rate for {}", rateName);
        List<RateDto> firstRawRateList = rateService.getRawRatesIfContains(rateName.substring(0, 3));
        List<RateDto> secondRawRateList = rateService.getRawRatesIfContains(rateName.substring(3, 6));
        if (firstRawRateList.isEmpty() || secondRawRateList.isEmpty()) {
            calculatorLogger.error("Raw rate data missing for derived rate calculation: {}", rateName);
            return null;
        }
        double[] firstRateBids = new double[firstRawRateList.size()];
        double[] firstRateAsks = new double[firstRawRateList.size()];
        double[] secondRateBids = new double[secondRawRateList.size()];
        double[] secondRateAsks = new double[secondRawRateList.size()];
        for (int i = 0; i < firstRawRateList.size(); i++) {
            firstRateBids[i] = firstRawRateList.get(i).getBid();
        }
        for (int i = 0; i < secondRawRateList.size(); i++) {
            secondRateBids[i] = secondRawRateList.get(i).getBid();
        }
        for (int i = 0; i < firstRawRateList.size(); i++) {
            firstRateAsks[i] = firstRawRateList.get(i).getAsk();
        }
        for (int i = 0; i < secondRawRateList.size(); i++) {
            secondRateAsks[i] = secondRawRateList.get(i).getAsk();
        }
        double[] rateFields = (double[]) derivedRateCalculationMethod(firstRateBids, firstRateAsks, secondRateBids, secondRateAsks);
        if (rateFields == null || rateFields.length < 2) {
            calculatorLogger.warn("Groovy script returned null or invalid result");
            throw new IllegalStateException("Groovy script returned null or invalid result");
        }
        calculatorLogger.info("Derived Rate Calculated for {}", rateName);
        return new RateDto(rateName, rateFields[0], rateFields[1], secondRawRateList.get(0).getTimestamp());
    }
}
