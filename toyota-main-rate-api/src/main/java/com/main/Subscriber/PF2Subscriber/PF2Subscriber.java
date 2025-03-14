package com.main.Subscriber.PF2Subscriber;

import com.main.Configuration.PF2SubscriberConfig;
import com.main.Coordinator.CoordinatorInterface;
import com.main.Dto.RateDto;
import com.main.Dto.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("PF2")
public class PF2Subscriber extends Thread implements SubscriberInterface {

    private CoordinatorInterface coordinator;
    private final String subscriberName;
    private final String rateUrl;
    private final String serverUrl;
    private boolean status;
    private final List<String> subscribedRateList;
    private final RestTemplate restTemplate;
    private final Logger logger = LogManager.getLogger("SubscriberLogger");

    public PF2Subscriber() throws IOException {
        logger.info("Initializing PF2Subscriber");
        this.subscriberName = PF2SubscriberConfig.getSubscriberName();;
        this.serverUrl = PF2SubscriberConfig.getServerAddress() + "/api";
        this.rateUrl = this.serverUrl + "/rates";
        this.restTemplate = new RestTemplate();
        this.status = false;
        this.subscribedRateList = new ArrayList<>();
        logger.info("PF2Subscriber initialized");
    }

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

    @Override
    public void setCoordinator(CoordinatorInterface coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void connect(String platformName, String userid, String password) throws IOException {
        logger.info("Connecting to {}", serverUrl);
        this.status = true;
        coordinator.onConnect(platformName,status);
        logger.info("Connected to {}", serverUrl);
        this.start();
    }

    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        logger.info("Disconnecting from {}", serverUrl);
        this.status = false;
        coordinator.onDisConnect(platformName,status);
        logger.info("Disconnected from {}", serverUrl);
    }

    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is subscribing to {}", platformName,rateName);
        subscribedRateList.add(platformName + "_" + rateName);
        logger.info("{} subscribed to {}", platformName,rateName);
    }

    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is unsubscribing from {}", platformName,rateName);
        subscribedRateList.remove(platformName + "_" + rateName);
        logger.info("{} unsubscribed to {}", platformName,rateName);
    }

    @Override
    public void run(){
        while(status){
            for(String rateName : subscribedRateList){
                String rateRequestURL = this.rateUrl + "/" + rateName;
                ResponseEntity<RateDto> response = restTemplate.getForEntity(rateRequestURL, RateDto.class);
                RateDto rateDto = response.getBody();
                switch(coordinator.onRateStatus(this.subscriberName, rateDto.getRateName())){
                    case RateStatus.NOT_AVAILABLE:
                        coordinator.onRateAvailable(this.subscriberName,rateDto.getRateName(),rateDto);
                        break;
                    case RateStatus.AVAILABLE:
                        coordinator.onRateUpdate(this.subscriberName,rateDto.getRateName(),rateDto);
                        break;
                    case RateStatus.UPDATED:
                        coordinator.onRateUpdate(this.subscriberName,rateDto.getRateName(),rateDto);
                        break;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("PF2Subscriber Error : {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }
}
