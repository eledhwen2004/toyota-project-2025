package com.main.RateCalculator;

import com.main.Database.PostgresqlDatabase;
import com.main.Dto.RateDto;
import com.main.Cache.RateCache;
import com.main.Services.RateService;
import com.main.Services.RateServiceInterface;
import groovy.lang.GroovyClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RateCalculator {
    private RateServiceInterface rateService;
    private String [] rawRateNames;
    private String [] derivedRates;
    private final Logger logger = LogManager.getLogger("CalculatorLogger");
    public final String rawRatecalculationScriptUrl = System.getProperty("user.dir") + "/toyota-main-rate-api/Scripts/RateCalculation/RawRateCalculationScript.groovy";
    public final String derivedRateCalculationScriptUrl = System.getProperty("user.dir") + "/toyota-main-rate-api/Scripts/RateCalculation/DerivedRateCalculationScript.groovy";

    public RateCalculator(RateServiceInterface rateService, String [] rawRates, String [] derivedRates) {
        this.rawRateNames = rawRates;
        this.derivedRates = derivedRates;
        this.rateService = rateService;
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
        return null;
    }

    private RateDto calculateRawRate(String rateName) {
        logger.info("Calculating Raw Rate for {}", rateName);
        List <RateDto> rawRateList = rateService.getRawRatesIfContains(rateName);
        if(rawRateList.isEmpty()) {
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
        logger.info("Raw Rate Calculated for {}", rateName);
        return new RateDto(rateName,rateFields[0],rateFields[1], rawRateList.getFirst().getTimestamp());
    }

    private RateDto calculateDerivedRate(String rateName) {
        logger.info("Calculating Derived Rate for  {}",rateName);
        List<RateDto> firstRawRateList = rateService.getRawRatesIfContains(rateName.substring(0,3));
        List<RateDto> secondRawRateList = rateService.getRawRatesIfContains(rateName.substring(3,6));
        if (firstRawRateList.isEmpty() || secondRawRateList.isEmpty()) {
            logger.error("Raw rate data missing for derived rate calculation: {}", rateName);
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
            throw new IllegalStateException("Groovy script returned null or invalid result");
        }
        logger.info("Derived Rate Calculated for {}", rateName);
        return new RateDto(rateName,rateFields[0],rateFields[1],secondRawRateList.getFirst().getTimestamp());
    }

}

