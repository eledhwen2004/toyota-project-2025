package com.main.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SubscriberConfig {

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

    public static String[] getServerAddresses() throws IOException {
        Properties properties = loadProperties();
        return properties.getProperty("server_addresses").split(",");
    }
}
