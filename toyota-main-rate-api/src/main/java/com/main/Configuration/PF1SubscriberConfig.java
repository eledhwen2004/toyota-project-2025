package com.main.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PF1SubscriberConfig {
    private static String path = System.getProperty("user.dir") + "/toyota-main-rate-api/src/main/java/com/main/Subscriber/PF1Subscriber/config.properties";
    String getPath(){
        return this.path;
    }

    public static String getSubscriberName() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("subscriber_name");
    }

    public static String getServerAdress() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return properties.getProperty("server_adress");
    }

    public static int getPort() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        return Integer.parseInt(properties.getProperty("port"));
    }
}
