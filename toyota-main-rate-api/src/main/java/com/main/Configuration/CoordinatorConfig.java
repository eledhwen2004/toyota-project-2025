package com.main.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CoordinatorConfig {
    private static String path = System.getProperty("user.dir") + "/toyota-main-rate-api/src/main/resources/configFiles/coordinator.properties";
    String getPath(){
        return this.path;
    }

    public static String[]getSubscriberNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("subscriber_names").split(",");
    }

    public static String[]getRawRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("raw_rates").split(",");
    }

    public static String[]getDerivedRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("derived_rates").split(",");
    }

    public static String[]getCalculatedRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String [] derivedRates = properties.getProperty("derived_rates").split(",");
        String [] rawRateNames = properties.getProperty("raw_rates").split(",");
        String [] calculatedRateNames = new String[derivedRates.length + rawRateNames.length];
        for(int i = 0; i < derivedRates.length; i++){
            calculatedRateNames[i] = derivedRates[i];
        }
        for(int i = 0; i < rawRateNames.length; i++){
            calculatedRateNames[i+ derivedRates.length] = rawRateNames[i];
        }
        return calculatedRateNames;
    }
}
