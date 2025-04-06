package com.main.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PF2SubscriberConfig {
    private static String path = System.getProperty("user.dir") + "/toyota-main-rate-api/src/main/resources/configFiles/PF2Subscriber.properties";
    String getPath(){
        return this.path;
    }

    public static String getServerAddress() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("server_address");
    }

    public static String getSubscriberName() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("subscriber_name");
    }
}
