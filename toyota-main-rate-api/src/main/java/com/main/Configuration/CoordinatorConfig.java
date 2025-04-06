package com.main.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CoordinatorConfig {
    private static String path = System.getProperty("user.dir") + "/toyota-main-rate-api/src/main/resources/configFiles/coordinator.properties";
    String getPath(){
        return this.path;
    }

    public static String[]getSubscriberNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("subscriber_names").split(",");
    }

    public static String[]getRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("rate_names").split(",");
    }

    public static String[]getRawRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("raw_rates").split(",");
    }

    public static String[]getDerivedRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("derived_rates").split(",");
    }

    public static String[]getCalculatedRateNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("calculated_rates").split(",");
    }
}
