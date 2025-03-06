package com.main.Subscriber.PF2Subscriber;

import com.main.Configuration.PF1SubscriberConfig;
import com.main.Configuration.PF2SubscriberConfig;
import com.main.Dto.RateDto;
import com.main.Subscriber.SubscriberInterface;
import org.apache.kafka.common.metrics.stats.Rate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("PF2")
public class PF2Subscriber extends Thread implements SubscriberInterface {

    private final String subscriberName;
    private final String rateUrl;
    private final String serverUrl;
    private boolean status;
    private final List<String> subscribedRateList;
    private final RestTemplate restTemplate;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getRateUrl() {
        return rateUrl;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public PF2Subscriber() throws IOException {
        this.subscriberName = PF2SubscriberConfig.getSubscriberName();;
        this.serverUrl = PF2SubscriberConfig.getServerAddress() + "/api";
        this.rateUrl = this.serverUrl + "/rates";
        this.restTemplate = new RestTemplate();
        this.status = false;
        this.subscribedRateList = new ArrayList<>();
    }

    @Override
    public void connect(String platformName, String userid, String password) throws IOException {
        System.out.println("Connecting to : " + serverUrl);
        this.status = true;
        this.start();
    }

    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        this.status = false;
    }

    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        System.out.println("Subscribing to " + rateName);
        subscribedRateList.add(platformName + "_" + rateName);
    }

    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        System.out.println("Unsubscribing to " + rateName);
        subscribedRateList.remove(platformName + "_" + rateName);
    }

    @Override
    public void run(){
        while(status){
            for(String rateName : subscribedRateList){
                String rateRequestURL = this.rateUrl + "/" + rateName;
                ResponseEntity<RateDto> response = restTemplate.getForEntity(rateRequestURL, RateDto.class);
                RateDto rate = response.getBody();
                assert rate != null;
                System.out.println(rate.getRateName()+"\\|"+rate.getAsk()+"\\|"+rate.getBid()+"\\|"+rate.getTimestamp());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
