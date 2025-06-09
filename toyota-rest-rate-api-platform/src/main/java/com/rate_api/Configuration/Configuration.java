package com.rate_api.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading rate-related configuration values from the {@code config.properties} file.
 * <p>
 * This class provides access to predefined rate names, initial bid/ask values, and update frequency,
 * all loaded dynamically from the application's classpath.
 */
public class Configuration {

    /**
     * Loads properties from the {@code config.properties} file.
     *
     * @return a {@link Properties} object containing all the configuration values
     * @throws IOException if the file is not found or fails to load
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
     * Retrieves the list of rate names defined in the configuration.
     *
     * @return an array of rate name strings (e.g., {"USDTRY", "EURTRY"})
     * @throws IOException if configuration loading fails
     */
    public static String[] getRateNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("rate_names").split(",");
    }

    /**
     * Retrieves the initial "ask" value for a specific rate.
     *
     * @param rateName the name of the rate (e.g., "USDTRY")
     * @return the ask value as a {@link Double}
     * @throws IOException if configuration loading fails or the rate name is not found
     */
    public static Double getFirstRateAsk(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[0]);
    }

    /**
     * Retrieves the initial "bid" value for a specific rate.
     *
     * @param rateName the name of the rate (e.g., "USDTRY")
     * @return the bid value as a {@link Double}
     * @throws IOException if configuration loading fails or the rate name is not found
     */
    public static Double getFirstRateBid(String rateName) throws IOException {
        Properties properties = loadProperties();
        String[] askBidStr = properties.getProperty(rateName).split(",");
        return Double.parseDouble(askBidStr[1]);
    }

    /**
     * Retrieves the frequency at which rates should be updated.
     *
     * @return the frequency value in seconds or milliseconds depending on usage context
     * @throws IOException if configuration loading fails or property is missing
     */
    public static Double getRateFrequency() throws IOException {
        Properties properties = loadProperties();
        return Double.parseDouble(properties.getProperty("rate_frequency"));
    }
}
