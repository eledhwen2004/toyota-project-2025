package com.main.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static String path = System.getProperty("user.dir") + "/toyota-tcp-rate-api-platform/src/main/java/com/main/config.properties";

    public static String[] getRateNames() throws IOException {
        Properties properties = new Properties();
        System.out.println(path);
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("rate_names").split(",");
    }

    public static Double getFirstRateAsk(String rateName) throws IOException {
        Properties properties = new Properties();
        System.out.println(path);
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String []askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[0]);
    }

    public static Double getFirstRateBid(String rateName) throws IOException {
        Properties properties = new Properties();
        System.out.println(path);
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String []askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[1]);
    }

    public static Double getRateFrequency() throws IOException {
        Properties properties = new Properties();
        System.out.println(path);
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return Double.parseDouble(properties.getProperty("rate_frequency"));
    }

}
