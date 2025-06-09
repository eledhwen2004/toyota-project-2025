package com.main.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class responsible for loading application configuration from the {@code config.properties} file.
 * <p>
 * This class provides methods to retrieve rate-related configuration such as initial ask/bid values and update frequency.
 */
public class Configuration {

    /**
     * Loads the {@code config.properties} file from the classpath and returns a populated {@link Properties} object.
     *
     * @return the loaded {@link Properties} instance
     * @throws IOException if the file is not found or cannot be read
     */
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

    /**
     * Retrieves the list of rate names defined in the properties file under the {@code rate_names} key.
     *
     * @return an array of rate names
     * @throws IOException if the configuration file is not found or the property is missing
     */
    public static String[] getRateNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("rate_names").split(",");
    }

    /**
     * Retrieves the initial ask price for a given rate name from the properties file.
     *
     * @param rateName the name of the rate
     * @return the initial ask price
     * @throws IOException if the configuration file is not found or the rate entry is invalid
     */
    public static Double getFirstRateAsk(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[0]);
    }

    /**
     * Retrieves the initial bid price for a given rate name from the properties file.
     *
     * @param rateName the name of the rate
     * @return the initial bid price
     * @throws IOException if the configuration file is not found or the rate entry is invalid
     */
    public static Double getFirstRateBid(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[1]);
    }

    /**
     * Retrieves the rate update frequency (in seconds) defined under the {@code rate_frequency} key.
     *
     * @return the update frequency as a {@code Double}
     * @throws IOException if the configuration file is not found or the property is missing
     */
    public static Double getRateFrequency() throws IOException {
        Properties properties = loadProperties();
        return Double.parseDouble(properties.getProperty("rate_frequency"));
    }
}
