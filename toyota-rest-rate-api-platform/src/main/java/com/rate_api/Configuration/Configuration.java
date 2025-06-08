package com.rate_api.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("config.properties file not found in classpath");
            }
            properties.load(input);
        }
        return properties;
    }

    public static String[] getRateNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("rate_names").split(",");
    }

    public static Double getFirstRateAsk(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[0]);
    }

    public static Double getFirstRateBid(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[1]);
    }

    public static Double getRateFrequency() throws IOException {
        Properties properties = loadProperties();
        return Double.parseDouble(properties.getProperty("rate_frequency"));
    }
}
