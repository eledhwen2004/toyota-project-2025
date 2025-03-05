package com.rate_api.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static String path = System.getProperty("user.dir") + "/toyota-rest-rate-api-platform/src/main/resources/config.properties";

    public static String[] getRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("rate_names").split(",");
    }

    public static Double getFirstRateAsk(String rateName) throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String []askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[0]);
    }

    public static Double getFirstRateBid(String rateName) throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String []askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[1]);
    }

    public static Double getRateFrequency() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return Double.parseDouble(properties.getProperty("rate_frequency"));
    }

}
