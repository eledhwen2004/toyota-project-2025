package com.main.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SubscriberConfig {
    private static String path = System.getProperty("user.dir") + "/toyota-main-rate-api/src/main/resources/configFiles/subscriber.properties";
    String getPath(){
        return this.path;
    }

    public static String [] getServerAddresses() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String [] serverAdresses = properties.getProperty("server_addresses").split(",");
        return serverAdresses;
    }


}
