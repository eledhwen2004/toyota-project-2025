package com.main.Subscriber;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface SubscriberInterface {
    // Bağlantıyı gerçekleştirmek için olan metod
    void connect(String platformName, String userid, String password) throws IOException;
    // Bağlantıyı kesmek için olan metod
    void disConnect(String platformName, String userid, String password) throws IOException;
    // Bir rate e subscribe olmak için çağrılacak metod
    void subscribe(String platformName, String rateName) throws IOException;
    // Bir rate e subscription ı bitirmek için çağrılacak metod
    void unSubscribe(String platformName, String rateName) throws IOException;
}
