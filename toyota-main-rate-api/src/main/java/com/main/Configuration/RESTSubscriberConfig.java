package com.main.Configuration;

import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The RESTSubscriberConfig class is responsible for reading configuration data
 * related to REST subscriber settings from a properties file. It provides methods
 * for fetching the list of server addresses and subscriber names.
 * <p>
 * This class loads the configuration from the "RESTSubscriber.properties" file
 * located in the application's resources directory.
 * </p>
 */
public class RESTSubscriberConfig {
    private static String path = "./configFiles/RESTSubscriber.properties";

    /**
     * Retrieves the path of the configuration file.
     *
     * @return the path of the RESTSubscriber.properties file.
     */
    String getPath() {
        return this.path;
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
     * Retrieves the list of server addresses from the configuration file.
     * The addresses are expected to be comma-separated in the "server_addresses"
     * property.
     * <p>
     * This method loads the configuration file and parses the "server_addresses"
     * property into an array of strings representing server addresses.
     * </p>
     *
     * @return an array of server addresses.
     * @throws IOException if there is an issue reading the configuration file.
     */
    public static String[] getServerAddresses() throws IOException {
        Properties properties = loadProperties();
        String[] serverAddresses = properties.getProperty("server_addresses").split(",");
        return serverAddresses;
    }

    /**
     * Retrieves the list of subscriber names from the configuration file.
     * The names are expected to be comma-separated in the "subscriber_names"
     * property.
     * <p>
     * This method loads the configuration file and parses the "subscriber_names"
     * property into an array of strings representing the subscriber names.
     * </p>
     *
     * @return an array of subscriber names.
     * @throws IOException if there is an issue reading the configuration file.
     */
    public static String[] getSubscriberNames() throws IOException {
        Properties properties = loadProperties();
        String[] subscriberNames = properties.getProperty("subscriber_names").split(",");
        return subscriberNames;
    }
}
