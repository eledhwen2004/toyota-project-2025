package com.main.Configuration;

import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The CoordinatorConfig class is responsible for reading configuration values from
 * the `coordinator.properties` file. These values are used to configure various
 * parameters such as subscriber names, rate names, and other configuration settings
 * needed for the Coordinator's operation.
 */
public class CoordinatorConfig {

    // Path to the configuration properties file
    private static String path = "./configFiles/coordinator.properties";

    /**
     * Gets the path to the configuration file.
     *
     * @return The path to the `coordinator.properties` file.
     */
    String getPath(){
        return path;
    }

    /**
     * Loads the properties from a configuration file.
     * This method loads properties from the file specified by the class loader's resource path.
     * If the property file is not found in the classpath, an IOException is thrown.
     *
     * @return A {@link Properties} object containing the loaded properties from the file.
     * @throws IOException If an I/O error occurs while reading the property file or if the file is not found in the classpath.
     */
    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Property file 'coordinator.properties' not found in classpath.");
            }
            properties.load(input);
        }
        return properties;
    }


    /**
     * Retrieves the list of subscriber names from the configuration file.
     *
     * @return An array of subscriber names defined in the `coordinator.properties` file.
     * @throws IOException If an error occurs while reading the configuration file.
     */
    public static String[] getSubscriberNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("subscriber_names").split(",");
    }

    /**
     * Retrieves the list of raw rate names from the configuration file.
     *
     * @return An array of raw rate names defined in the `coordinator.properties` file.
     * @throws IOException If an error occurs while reading the configuration file.
     */
    public static String[] getRawRateNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("raw_rates").split(",");
    }

    /**
     * Retrieves the list of derived rate names from the configuration file.
     *
     * @return An array of derived rate names defined in the `coordinator.properties` file.
     * @throws IOException If an error occurs while reading the configuration file.
     */
    public static String[] getDerivedRateNames() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("derived_rates").split(",");
    }

    /**
     * Retrieves the combined list of calculated rate names, which includes both
     * raw rate names and derived rate names, from the configuration file.
     *
     * @return An array containing the combined list of calculated rate names.
     * @throws IOException If an error occurs while reading the configuration file.
     */
    public static String[] getCalculatedRateNames() throws IOException {
        Properties properties = loadProperties();

        // Get the derived and raw rate names
        String[] derivedRates = properties.getProperty("derived_rates").split(",");
        String[] rawRateNames = properties.getProperty("raw_rates").split(",");

        // Create the combined list of calculated rates
        String[] calculatedRateNames = new String[derivedRates.length + rawRateNames.length];

        // Fill in the derived rates
        for (int i = 0; i < derivedRates.length; i++) {
            calculatedRateNames[i] = derivedRates[i];
        }

        // Fill in the raw rates
        for (int i = 0; i < rawRateNames.length; i++) {
            calculatedRateNames[i + derivedRates.length] = rawRateNames[i];
        }

        return calculatedRateNames;
    }
}
