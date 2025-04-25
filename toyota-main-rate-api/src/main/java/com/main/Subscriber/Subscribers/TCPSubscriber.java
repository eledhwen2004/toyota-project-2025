package com.main.Subscriber.Subscribers;

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
public class TCPSubscriber extends Thread implements SubscriberInterface {

    private CoordinatorInterface coordinator;
    private final String subscriberName;
    private final String serverAddress;
    private final int serverPort;
    private Socket connectionSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Boolean connectionStatus;
    private final List<String> subscribedRateList;
    private final Logger logger = LogManager.getLogger("SubscriberLogger");


    public TCPSubscriber(String subscriberName, String serverAddress, int serverPort) throws IOException {
        logger.info("Initializing"+ subscriberName +"Subscriber");
        this.subscribedRateList = new ArrayList<>();
        this.subscriberName = subscriberName;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        logger.info(subscriberName + "Subscriber Initialized");
    }

    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    @Override
    public void connect(String platformName, String userName, String password) throws IOException {
        logger.info("Connecting to {} : {}", serverAddress,serverPort);
        this.connectionSocket = new Socket(serverAddress,serverPort);
        this.setConnectionStatus(true);
        this.reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.writer = new PrintWriter(connectionSocket.getOutputStream(),true);
        writer.println(userName+"|"+password);
        if(!connectionSocket.isConnected()){
            connectionSocket.close();
            System.out.println("Connection closed");
            System.out.println("Wrong userName or password");
        }
        logger.info("PF1Subscriber Connected to {} : {}", serverAddress,serverPort);
        coordinator.onConnect(platformName, connectionStatus);
        this.start();
    }

    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        logger.info("Disconnecting from {}", serverAddress);
        this.writer.close();
        this.reader.close();
        this.setConnectionStatus(false);
        coordinator.onDisConnect(platformName, connectionStatus);
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
        while(connectionStatus){
            try {
                response = reader.readLine();
                RateDto rateDto = RateMapper.stringToRateDto(response);
                for(String subsribedRateName : subscribedRateList ){
                    if(rateDto.getRateName().equals(this.subscriberName+"_"+subsribedRateName)){
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
                }

            } catch (IOException e) {
                logger.error(this.subscriberName+ "Subscriber Error : {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
