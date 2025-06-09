package com.main.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration utility class for reading coordinator-related settings from a properties file.
 * <p>
 * This class loads the {@code coordinator.properties} file from the classpath and provides
 * access to subscriber and rate configuration entries.
 */
public class CoordinatorConfig {

    /**
     * Loads the {@code coordinator.properties} file from the classpath and returns it as a {@link Properties} object.
     *
     * @return loaded properties
     * @throws IOException if the file is not found or cannot be read
     */
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

    /**
     * Retrieves the list of subscriber class names defined in the {@code subscriber_names} property.
     *
     * @return array of subscriber names
     * @throws IOException if properties file cannot be loaded
     */
    public static String[] getSubscriberNames() throws IOException {
        return loadProperties().getProperty("subscriber_names").split(",");
    }

    /**
     * Retrieves the list of raw rate names defined in the {@code raw_rates} property.
     *
     * @return array of raw rate names
     * @throws IOException if properties file cannot be loaded
     */
    public static String[] getRawRateNames() throws IOException {
        return loadProperties().getProperty("raw_rates").split(",");
    }

    /**
     * Retrieves the list of derived rate names defined in the {@code derived_rates} property.
     *
     * @return array of derived rate names
     * @throws IOException if properties file cannot be loaded
     */
    public static String[] getDerivedRateNames() throws IOException {
        return loadProperties().getProperty("derived_rates").split(",");
    }

    /**
     * Returns the union of raw and derived rate names as the complete list of calculated rates.
     *
     * @return array of all calculated rate names
     * @throws IOException if properties file cannot be loaded
     */
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
