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

public class RateCalculator {
    private RateCache rateCache;
    private String [] rawRateNames;
    private String [] derivedRates;
    private final Logger logger = LogManager.getLogger("CalculatorLogger");
    public final String rawRateCalculationScriptUrl = "RawRateCalculationScript.groovy";
    public final String derivedRateCalculationScriptUrl = "DerivedRateCalculationScript.groovy";

    public RateCalculator(RateCache rateCache, String [] rawRates, String [] derivedRates) {
        this.rawRateNames = rawRates;
        this.derivedRates = derivedRates;
        this.rateCache = rateCache;
        logger.info("RateCalculator initialized with {} raw rates and {} derived rates",
                rawRates.length, derivedRates.length);

    }

    public double[] rawRateCalculationMethod(double[] asks, double[] bids) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(rawRateCalculationScriptUrl);
            if (inputStream == null) {
                throw new FileNotFoundException("Raw Groovy script not found in classpath: " + rawRateCalculationScriptUrl);
            }

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

    public double[] derivedRateCalculationMethod(double[] firstRateBids, double[] firstRateAsks, double[] secondRateBids, double[] secondRateAsks) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(derivedRateCalculationScriptUrl);
            if (inputStream == null) {
                throw new FileNotFoundException("Derived Groovy script not found in classpath: " + derivedRateCalculationScriptUrl);
            }

            File tempScript = File.createTempFile("DerivedRateCalculationScript", ".groovy");
            try (FileOutputStream out = new FileOutputStream(tempScript)) {
                inputStream.transferTo(out);
            }

            Class<?> groovyClass = groovyClassLoader.parseClass(tempScript);
            Object groovyObject = groovyClass.getDeclaredConstructor().newInstance();
            Method method = groovyClass.getMethod("calculate", double[].class, double[].class, double[].class, double[].class);

            return (double[]) method.invoke(groovyObject, firstRateBids, firstRateAsks, secondRateBids, secondRateAsks);

        } catch (Exception e) {
            logger.error("Error in derivedRateCalculationMethod", e);
        }
        return null;
    }



    public RateDto calculateRate(String rateName) {
        logger.info("Calculating rate for: {}", rateName);
        for(String rawRateName : rawRateNames) {
            if(rawRateName.equals(rateName)) {
                return calculateRawRate(rateName);
            }
        }
        for(String derivedRate : derivedRates) {
            if(derivedRate.equals(rateName)) {
                return calculateDerivedRate(rateName);
            }
        }
        logger.warn("Requested rate {} not found in raw or derived rate lists", rateName);
        return null;
    }

    private RateDto calculateRawRate(String rateName) {
        logger.info("Calculating Raw Rate for {}", rateName);
        List <RateDto> rawRateList = rateCache.getRawRatesIfContains(rateName);
        if(rawRateList.isEmpty()) {
            logger.warn("No raw rate data found in cache for: {}", rateName);
            return null;
        }
        double [] bids = new double[rawRateList.size()];
        double [] asks = new double[rawRateList.size()];
        for (int i = 0; i < rawRateList.size(); i++) {
            bids[i] = rawRateList.get(i).getBid();
        }
        for(int i = 0; i < rawRateList.size(); i++) {
            asks[i] = rawRateList.get(i).getAsk();
        }
        double [] rateFields = (double[]) rawRateCalculationMethod(bids,asks);
        if (rateFields == null || rateFields.length < 2) {
            logger.error("Raw rate calculation returned null or invalid result for {}", rateName);
            return null;
        }
        logger.info("Raw Rate Calculated for {}", rateName);
        return new RateDto(rateName,rateFields[0],rateFields[1], rawRateList.getFirst().getTimestamp());
    }

    private RateDto calculateDerivedRate(String rateName) {
        logger.info("Calculating Derived Rate for  {}",rateName);
        List<RateDto> firstRawRateList = rateCache.getRawRatesIfContains(rateName.substring(0,3));
        List<RateDto> secondRawRateList = rateCache.getRawRatesIfContains(rateName.substring(3,6));

        if (firstRawRateList == null || secondRawRateList == null ||
            firstRawRateList.isEmpty() || secondRawRateList.isEmpty()) {
            logger.error("Raw rate data missing or null for derived rate calculation: {}", rateName);
            return null;
        }

        double []firstRateBids = new double[firstRawRateList.size()];
        double []firstRateAsks = new double[firstRawRateList.size()];
        double []secondRateBids = new double[secondRawRateList.size()];
        double []secondRateAsks = new double[secondRawRateList.size()];
        for(int i = 0;i<firstRawRateList.size();i++) {
            firstRateBids[i] = firstRawRateList.get(i).getBid();
        }
        for(int i = 0;i<secondRawRateList.size();i++) {
            secondRateBids[i] = secondRawRateList.get(i).getBid();
        }
        for(int i = 0;i<firstRawRateList.size();i++) {
            firstRateAsks[i] = firstRawRateList.get(i).getAsk();
        }
        for(int i = 0;i<secondRawRateList.size();i++) {
            secondRateAsks[i] = secondRawRateList.get(i).getAsk();
        }
        double [] rateFields = (double[]) derivedRateCalculationMethod(firstRateBids,firstRateAsks,secondRateBids,secondRateAsks);
        if (rateFields == null || rateFields.length < 2) {
            logger.error("Derived rate calculation failed or returned invalid result for {}", rateName);
            return null;
        }
        logger.info("Derived Rate Calculated for {}", rateName);
        return new RateDto(rateName,rateFields[0],rateFields[1],secondRawRateList.getFirst().getTimestamp());
    }

}

