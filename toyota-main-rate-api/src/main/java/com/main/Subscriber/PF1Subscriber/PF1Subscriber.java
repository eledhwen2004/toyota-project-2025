package com.main.Subscriber.PF1Subscriber;

import com.main.Configuration.PF1SubscriberConfig;
import com.main.Coordinator.CoordinatorInterface;
import com.main.Dto.RateDto;
import com.main.Mapper.RateMapper;
import com.main.Subscriber.RateStatus;
import com.main.Subscriber.SubscriberInterface;
import org.apache.kafka.common.metrics.stats.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Service("PF1")
public class PF1Subscriber extends Thread implements SubscriberInterface {

    private CoordinatorInterface coordinator;
    private String subscriberName;
    private String serverAddress;
    private int serverPort;
    private Socket connectionSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Boolean status;
    private List<String> subscribedRateList;

    public PF1Subscriber() throws IOException {
        this.subscribedRateList = new ArrayList<>();
        this.setSubscriberName(PF1SubscriberConfig.getSubscriberName());
        this.setServerAddress(PF1SubscriberConfig.getServerAdress());
        this.setServerPort(PF1SubscriberConfig.getPort());
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    private void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    private void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    private void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void setCoordinator(CoordinatorInterface coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void connect(String platformName, String userName, String password) throws IOException {
        System.out.println("Connecting to " + serverAddress + ":" + serverPort);
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
        coordinator.onConnect(platformName,status);
        this.start();
    }

    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        this.writer.close();
        this.reader.close();
        this.setStatus(false);
        coordinator.onDisConnect(platformName,status);
        this.connectionSocket.close();
        System.out.println("Connection closed with :"+ this.subscriberName);
    }

    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        System.out.println("Subscribing to " + platformName + "_" +rateName);
        this.writer.println("subscribe|"+platformName+"_"+rateName);
        this.subscribedRateList.add(rateName);
    }

    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        System.out.println("Unsubscribing to " + rateName);
        this.writer.println("unsubscribe|"+platformName+"_"+rateName);
        this.subscribedRateList.remove(rateName);
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
                throw new RuntimeException(e);
            }
        }
    }
}
