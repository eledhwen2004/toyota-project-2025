package com.main.Subscriber.PF2Subscriber;

import com.main.Subscriber.SubscriberInterface;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("PF2")
public class PF2Subscriber implements SubscriberInterface {
    @Override
    public void connect(String platformName, String userid, String password) throws IOException {

    }

    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {

    }

    @Override
    public void subscribe(String platformName, String rateName) throws IOException {

    }

    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {

    }
}
