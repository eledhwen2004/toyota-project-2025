package com.main.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CoordinatorConfig {

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = CoordinatorConfig.class.getClassLoader().getResourceAsStream("coordinator.properties")) {
            if (input == null) {
                throw new IOException("coordinator.properties not found in classpath");
            }
            properties.load(input);
        }
        return properties;
    }

    public static String[] getSubscriberNames() throws IOException {
        return loadProperties().getProperty("subscriber_names").split(",");
    }

    public static String[] getRawRateNames() throws IOException {
        return loadProperties().getProperty("raw_rates").split(",");
    }

    public static String[] getDerivedRateNames() throws IOException {
        return loadProperties().getProperty("derived_rates").split(",");
    }

    public static String[] getCalculatedRateNames() throws IOException {
        Properties properties = loadProperties();
        String[] derivedRates = properties.getProperty("derived_rates").split(",");
        String[] rawRateNames = properties.getProperty("raw_rates").split(",");
        String[] calculatedRateNames = new String[derivedRates.length + rawRateNames.length];

        System.arraycopy(derivedRates, 0, calculatedRateNames, 0, derivedRates.length);
        System.arraycopy(rawRateNames, 0, calculatedRateNames, derivedRates.length, rawRateNames.length);

        return calculatedRateNames;
    }
}
