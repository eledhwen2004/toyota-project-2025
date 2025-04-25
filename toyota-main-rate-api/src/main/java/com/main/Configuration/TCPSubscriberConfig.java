package com.main.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TCPSubscriberConfig {
    private static String path = System.getProperty("user.dir") + "/toyota-main-rate-api/src/main/resources/configFiles/TCPSubscriber.properties";
    String getPath(){
        return this.path;
    }

    public static String [] getSubscriberNames() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String [] subscriberNames = properties.getProperty("subscriber_names").split(",");
        return subscriberNames;
    }

    public static String [] getServerAdresses() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String [] serverAdresses = properties.getProperty("server_adresses").split(",");
        return serverAdresses;
    }

    public static int [] getPorts() throws IOException {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(path);
        properties.load(input);
        String []portsAsString = properties.getProperty("ports").split(",");
        int [] ports = new int [portsAsString.length];
        for(int i = 0; i < portsAsString.length; i++){
            ports [i] = Integer.parseInt(portsAsString[i]);
        }
        return ports;
    }
}
