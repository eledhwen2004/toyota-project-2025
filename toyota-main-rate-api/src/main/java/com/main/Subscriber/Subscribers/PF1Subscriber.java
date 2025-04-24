package com.main.Subscriber.Subscribers;

import com.main.Configuration.PF1SubscriberConfig;
import com.main.Coordinator.CoordinatorInterface;
import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import com.main.Dto.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PF1Subscriber extends Thread implements SubscriberInterface {

    private CoordinatorInterface coordinator;
    private final String subscriberName;
    private final String serverAddress;
    private final int serverPort;
    private Socket connectionSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Boolean status;
    private final List<String> subscribedRateList;
    private final Logger logger = LogManager.getLogger("SubscriberLogger");


    public PF1Subscriber() throws IOException {
        logger.info("Initializing PF1Subscriber");
        this.subscribedRateList = new ArrayList<>();
        this.subscriberName = PF1SubscriberConfig.getSubscriberName();
        this.serverAddress = PF1SubscriberConfig.getServerAdress();
        this.serverPort = PF1SubscriberConfig.getPort();
        logger.info("PF1Subscriber Initialized");
    }

    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    @Override
    public void connect(String platformName, String userName, String password) throws IOException {
        logger.info("Connecting to {} : {}", serverAddress,serverPort);
        this.connectionSocket = new Socket(serverAddress,serverPort);
        this.setStatus(true);
        this.reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.writer = new PrintWriter(connectionSocket.getOutputStream(),true);
        writer.println(userName+"|"+password);
        if(!connectionSocket.isConnected()){
            connectionSocket.close();
            System.out.println("Connection closed");
            System.out.println("Wrong userName or password");
        }
        logger.info("PF1Subscriber Connected to {} : {}", serverAddress,serverPort);
        coordinator.onConnect(platformName,status);
        this.start();
    }

    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        logger.info("Disconnecting from {}", serverAddress);
        this.writer.close();
        this.reader.close();
        this.setStatus(false);
        coordinator.onDisConnect(platformName,status);
        this.connectionSocket.close();
        logger.info("PF1Subscriber Disconnected from {}", serverAddress);
    }

    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is subscribing to {}", platformName,rateName);
        this.writer.println("subscribe|"+platformName+"_"+rateName);
        this.subscribedRateList.add(rateName);
        logger.info("{} is subscribed to {}", platformName,rateName);
    }

    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is unsubscribing from {}", platformName,rateName);
        System.out.println("Unsubscribing to " + rateName);
        this.writer.println("unsubscribe|"+platformName+"_"+rateName);
        this.subscribedRateList.remove(rateName);
        logger.info("{} is unsubscribed to {}", platformName,rateName);
    }

    @Override
    public void run() {
        String response;
        while(status){
            try {
                response = reader.readLine();
                RateDto rateDto = RateMapper.stringToRateDto(response);
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
            } catch (IOException e) {
                logger.error("PF1Subscriber Error : {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
