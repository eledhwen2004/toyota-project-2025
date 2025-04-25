package com.rate_api.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and provides access to configuration values defined in the 'config.properties' file.
 */
public class Configuration {

    /**
     * The path to the properties file within the classpath.
     */
    private static String path = "config.properties";

    /**
     * Loads properties from the 'config.properties' file.
     *
     * @return Properties object loaded from the configuration file.
     * @throws IOException if the file is not found or cannot be read.
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
     * Retrieves the list of rate names defined in the properties file.
     *
     * @return An array of rate name strings.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static String[] getRateNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("rate_names").split(",");
    }

    /**
     * Retrieves the ask value (first element) for a given rate name.
     *
     * @param rateName The name of the rate to retrieve.
     * @return The ask value as a Double.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static Double getFirstRateAsk(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[0]);
    }

    /**
     * Retrieves the bid value (second element) for a given rate name.
     *
     * @param rateName The name of the rate to retrieve.
     * @return The bid value as a Double.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static Double getFirstRateBid(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[1]);
    }

    /**
     * Retrieves the frequency value at which rates are updated.
     *
     * @return The rate frequency as a Double.
     * @throws IOException if an error occurs while reading the properties file.
     */
    public static Double getRateFrequency() throws IOException {
        Properties properties = loadProperties();
        return Double.parseDouble(properties.getProperty("rate_frequency"));
    }
}
