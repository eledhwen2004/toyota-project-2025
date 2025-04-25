package com.main.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides methods to load and retrieve properties from a configuration file.
 * The configuration file is named "config.properties" and is expected to be
 * located in the classpath.
 */
public class Configuration {

    /**
     * The path to the configuration file.
     */
    private static String path = "config.properties";

    /**
     * Loads the properties from the configuration file.
     *
     * @return a {@link Properties} object containing the configuration values.
     * @throws IOException if an error occurs while reading the properties file.
     */
    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Property file 'config.properties' not found in classpath.");
            }
            properties.load(input);
        }
        return properties;
    }

    /**
     * Retrieves the list of rate names from the configuration file.
     *
     * @return an array of rate names.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static String[] getRateNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("rate_names").split(",");
    }

    /**
     * Retrieves the first ask price for a given rate from the configuration file.
     *
     * @param rateName the name of the rate for which the ask price is to be fetched.
     * @return the first ask price as a {@link Double}.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static Double getFirstRateAsk(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[0]);
    }

    /**
     * Retrieves the first bid price for a given rate from the configuration file.
     *
     * @param rateName the name of the rate for which the bid price is to be fetched.
     * @return the first bid price as a {@link Double}.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static Double getFirstRateBid(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[1]);
    }

    /**
     * Retrieves the rate frequency value from the configuration file.
     *
     * @return the rate frequency as a {@link Double}.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static Double getRateFrequency() throws IOException {
        Properties properties = loadProperties();
        return Double.parseDouble(properties.getProperty("rate_frequency"));
    }
}
