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
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCPSubscriber represents a subscriber that connects to a server over TCP to receive and process rate updates.
 * It implements the SubscriberInterface and interacts with a CoordinatorInterface to manage subscriptions and rate updates.
 */
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

    /**
     * Constructor for initializing the TCPSubscriber.
     *
     * @param subscriberName The name of the subscriber.
     * @param serverAddress The address of the server to connect to.
     * @param serverPort The port on the server to connect to.
     * @throws IOException If an I/O error occurs during the initialization.
     */
    public TCPSubscriber(String subscriberName, String serverAddress, int serverPort) throws IOException {
        logger.info("Initializing"+ subscriberName +"Subscriber");
        this.subscribedRateList = new ArrayList<>();
        this.subscriberName = subscriberName;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        logger.info(subscriberName + "Subscriber Initialized");
    }

    /**
     * Sets the coordinator for the subscriber.
     *
     * @param coordinator The coordinator that will manage the subscriber's interactions.
     */
    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    /**
     * Returns the current connection status of the subscriber.
     *
     * @return true if the subscriber is connected, false otherwise.
     */
    @Override
    public boolean getConnectionStatus() {
        return this.connectionStatus;
    }

    /**
     * Connects to the server with the provided credentials.
     *
     * @param platformName The name of the platform being used.
     * @param userName The username for login.
     * @param password The password for login.
     * @throws IOException If an I/O error occurs during connection.
     */
    @Override
    public void connect(String platformName, String userName, String password) throws IOException {
        logger.info("Connecting to {} : {}", serverAddress,serverPort);
        try {
            this.connectionSocket = new Socket(serverAddress, serverPort);
        } catch (ConnectException e) {
            try {
                if (connectionSocket != null && !connectionSocket.isClosed()) {
                    connectionSocket.close();
                }
            } catch (IOException ex) {
                logger.error("Error while closing the socket", ex);
            }

            logger.error("Failed to connect to {}:{}", serverAddress, serverPort);
            this.setConnectionStatus(false);
            return;
        }

        this.setConnectionStatus(true);
        this.reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.writer = new PrintWriter(connectionSocket.getOutputStream(),true);
        writer.println(userName+"|"+password);
        if(!connectionSocket.isConnected()){
            connectionSocket.close();
            logger.error("Failed to connect to {}:{}",serverAddress,serverPort);
            System.out.println("Cannot connect to server");
            System.out.println("Wrong userName or password");
            return;
        }
        logger.info("PF1Subscriber Connected to {} : {}", serverAddress,serverPort);
        coordinator.onConnect(platformName, connectionStatus);
        this.start();
    }

    /**
     * Disconnects from the server and closes the connection.
     *
     * @param platformName The name of the platform being used.
     * @param userid The user's ID.
     * @param password The user's password.
     * @throws IOException If an I/O error occurs during disconnection.
     */
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

    /**
     * Subscribes to a rate on the server.
     *
     * @param platformName The name of the platform to subscribe to.
     * @param rateName The rate to subscribe to.
     * @throws IOException If an I/O error occurs while subscribing.
     */
    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is subscribing to {}", platformName,rateName);
        this.writer.println("subscribe|"+platformName+"_"+rateName);
        this.subscribedRateList.add(rateName);
        logger.info("{} is subscribed to {}", platformName,rateName);
    }

    /**
     * Unsubscribes from a rate on the server.
     *
     * @param platformName The name of the platform to unsubscribe from.
     * @param rateName The rate to unsubscribe from.
     * @throws IOException If an I/O error occurs while unsubscribing.
     */
    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is unsubscribing from {}", platformName,rateName);
        System.out.println("Unsubscribing to " + rateName);
        this.writer.println("unsubscribe|"+platformName+"_"+rateName);
        this.subscribedRateList.remove(rateName);
        logger.info("{} is unsubscribed to {}", platformName,rateName);
    }

    /**
     * Listens for incoming rate updates from the server and processes them.
     * The method checks if the received rate is one of the subscriber's subscribed rates and triggers the appropriate updates
     * through the coordinator based on the rate's status.
     */
    @Override
    public void run() {
        String response;
        while(connectionStatus){
            try {
                response = reader.readLine();
                if(response == null){
                    continue;  // If there's no data to read, skip this iteration
                }
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
                logger.error(this.subscriberName + "Subscriber Error: {}", e.getMessage());

                // Retry mechanism: Retry after a small delay (e.g., 2 seconds)
                try {
                    Thread.sleep(2000);  // Sleep for 2 seconds before trying again
                } catch (InterruptedException ex) {
                    logger.error(this.subscriberName + "Subscriber Error during sleep: {}", ex.getMessage());
                    // Optionally, handle interruption and continue
                }

                // Attempt to reconnect after failure (optional)
                if (!connectionSocket.isConnected()) {
                    logger.info(this.subscriberName + " Subscriber: Attempting to reconnect...");
                    try {
                        // Close the previous socket if any, and create a new one
                        if (connectionSocket != null && !connectionSocket.isClosed()) {
                            connectionSocket.close();
                        }
                        connect(subscriberName, "reconnectUser", "reconnectPassword"); // Pass appropriate credentials
                    } catch (IOException reconnectEx) {
                        logger.error(this.subscriberName + " Subscriber: Reconnection failed: {}", reconnectEx.getMessage());
                    }
                }
            } catch (Exception ex) {
                logger.error(this.subscriberName + " Subscriber Error: Unexpected error: {}", ex.getMessage());
                break;  // Exit the loop on unexpected errors (optional)
            }


        }
    }
}
