package com.main.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration utility class for loading subscriber-specific settings
 * from the {@code subscriber.properties} file.
 * <p>
 * Primarily used to retrieve server addresses to which subscribers will connect.
 */
public class SubscriberConfig {

    /**
     * Loads the {@code subscriber.properties} file from the classpath.
     *
     * @return the loaded {@link Properties} object
     * @throws IOException if the properties file is not found or cannot be read
     */
    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = SubscriberConfig.class.getClassLoader().getResourceAsStream("subscriber.properties")) {
            if (input == null) {
                throw new IOException("subscriber.properties not found in classpath");
            }
            properties.load(input);
        }
        return properties;
    }

    /**
     * Retrieves the list of subscriber server addresses defined in the {@code server_addresses} property.
     *
     * @return an array of server address strings
     * @throws IOException if the properties file cannot be loaded or the property is missing
     */
    public static String[] getServerAddresses() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("server_addresses").split(",");
    }
}
