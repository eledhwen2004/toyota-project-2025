package com.main.Subscriber.Subscribers;

import com.main.Authentication.LoginRequest;
import com.main.Authentication.UserAuth;
import com.main.Coordinator.CoordinatorInterface;
import com.main.Dto.RateDto;
import com.main.Dto.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RESTSubscriber extends Thread implements SubscriberInterface {

    private CoordinatorInterface coordinator;
    private final String subscriberName;
    private final String rateUrl;
    private final String loginUrl;
    private final String serverUrl;
    private boolean connectionStatus;
    private final List<String> subscribedRateList;
    private final RestTemplate restTemplate;
    private final Logger logger = LogManager.getLogger("SubscriberLogger");
    private int startNumber = 0;

    public RESTSubscriber(String subscriberName, String serverUrl) throws IOException {
        logger.info("Initializing"+ subscriberName +"Subscriber");
        this.subscriberName = subscriberName;
        this.serverUrl = serverUrl + "/api";
        this.rateUrl = this.serverUrl + "/rates";
        this.loginUrl = this.serverUrl + "/login";
        this.restTemplate = new RestTemplate();
        this.connectionStatus = false;
        this.subscribedRateList = new ArrayList<>();
        logger.info( subscriberName+ "Subscriber initialized");
    }

    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    @Override
    public void connect(String platformName, String userid, String password) throws IOException {
        logger.info("Connecting to {}", serverUrl);
        LoginRequest loginRequest = new LoginRequest(userid,password);
        ResponseEntity<UserAuth> userAuthResponse = restTemplate.postForEntity(this.loginUrl,loginRequest,UserAuth.class);
        UserAuth loginResponse = userAuthResponse.getBody();
        if(loginResponse.getStatus().equals("success")){
            System.out.println(loginResponse.getMessage());
        }
        else{
            System.out.println("status : " + loginResponse.getStatus());
            System.out.println("message : " + loginResponse.getMessage());
            return;
        }
        this.connectionStatus = true;
        coordinator.onConnect(platformName, connectionStatus);
        logger.info("Connected to {}", serverUrl);
        this.start();
    }

    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        logger.info("Disconnecting from {}", serverUrl);
        this.connectionStatus = false;
        coordinator.onDisConnect(platformName, connectionStatus);
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
        System.out.println("Start number is " + startNumber );
        while(connectionStatus){
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
