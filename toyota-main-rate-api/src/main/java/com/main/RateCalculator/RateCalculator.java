package com.main.RateCalculator;

import com.main.Dto.RateDto;
import com.main.Cache.RateCache;
import groovy.lang.GroovyClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class RateCalculator {
    private RateCache rateCache;
    private String [] rawRates;
    private String [] derivedRates;
    private final Logger logger = LogManager.getLogger("CalculatorLogger");
    public final String rawRatecalculationScriptUrl = System.getProperty("user.dir") + "/toyota-main-rate-api/Scripts/RateCalculation/RawRateCalculationScript.groovy";
    public final String derivedRateCalculationScriptUrl = System.getProperty("user.dir") + "/toyota-main-rate-api/Scripts/RateCalculation/DerivedRateCalculationScript.groovy";

    public RateCalculator(RateCache rateCache,String [] rawRates,String [] derivedRates) {
        this.rawRates = rawRates;
        this.derivedRates = derivedRates;
        this.rateCache = rateCache;
    }

    public double [] rawRateCalculationMethod(double [] asks,double [] bids) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try {
            File groovyClassFile = new File(rawRatecalculationScriptUrl); // Path to your compiled class file
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyClassFile);
            Object groovyObject = groovyClass.newInstance();
            Method method = groovyClass.getMethod("calculate", double[].class,double[].class);
            return (double[]) method.invoke(groovyObject, asks,bids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double[] derivedRateCalculationMethod(double []firstRateBids,double []firstRateAsks,double []secondRateBids,double []secondRateAsks){
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        try {
            File groovyClassFile = new File(derivedRateCalculationScriptUrl); // Path to your compiled class file
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyClassFile);
            Object groovyObject = groovyClass.newInstance();
            Method method = groovyClass.getMethod("calculate", double[].class,double[].class,double[].class,double[].class);
            return (double[]) method.invoke(groovyObject,firstRateBids,firstRateAsks,secondRateBids,secondRateAsks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public RateDto calculateRate(String rateName) {
        for(String rawRate : rawRates) {
            if(rawRate.equals(rateName)) {
                return calculateRawRate(rateName);
            }
        }
        for(String derivedRate : derivedRates) {
            if(derivedRate.equals(rateName)) {
                return calculateDerivedRate(rateName);
            }
        }
        return null;
    }

    private RateDto calculateRawRate(String rateName) {
        logger.info("Calculating Raw Rate for {}", rateName);
        List<RateDto> rawRates = rateCache.getRawRatesBySymbol(rateName);
        double [] bids = new double[rawRates.size()];
        double [] asks = new double[rawRates.size()];
        for (int i = 0; i < rawRates.size(); i++) {
            bids[i] = rawRates.get(i).getBid();
        }
        for(int i = 0; i < rawRates.size(); i++) {
            asks[i] = rawRates.get(i).getAsk();
        }
        double [] rateFields = (double[]) rawRateCalculationMethod(bids,asks);
        logger.info("Raw Rate Calculated for {}", rateName);
        return new RateDto(rateName,rateFields[0],rateFields[1], rawRates.getFirst().getTimestamp());
    }

    private RateDto calculateDerivedRate(String rateName) {
        logger.info("Calculating Derived Rate for  {}",rateName);
        List<RateDto> firstRawRates = rateCache.getRawRatesBySymbol(rateName.substring(0,3));
        List<RateDto> secondRawRates = rateCache.getRawRatesBySymbol(rateName.substring(3,6));
        double []firstRateBids = new double[firstRawRates.size()];
        double []firstRateAsks = new double[firstRawRates.size()];
        double []secondRateBids = new double[secondRawRates.size()];
        double []secondRateAsks = new double[secondRawRates.size()];
        for(int i = 0;i<firstRawRates.size();i++) {
            firstRateBids[i] = firstRawRates.get(i).getBid();
        }
        for(int i = 0;i<secondRawRates.size();i++) {
            secondRateBids[i] = secondRawRates.get(i).getBid();
        }
        for(int i = 0;i<firstRawRates.size();i++) {
            firstRateAsks[i] = firstRawRates.get(i).getAsk();
        }
        for(int i = 0;i<secondRawRates.size();i++) {
            secondRateAsks[i] = secondRawRates.get(i).getAsk();
        }
        double [] rateFields = (double[]) derivedRateCalculationMethod(firstRateBids,firstRateAsks,secondRateBids,secondRateAsks);
        logger.info("Derived Rate Calculated for {}", rateName);
        return new RateDto(rateName,rateFields[0],rateFields[1],firstRawRates.getFirst().getTimestamp());
    }

}

