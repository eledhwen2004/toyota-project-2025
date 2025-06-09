package com.main.Subscriber.Subscribers;

import com.main.Coordinator.CoordinatorInterface;
import com.main.Dto.RateDto;
import com.main.Dto.RateStatus;
import com.main.Mapper.RateMapper;
import com.main.Subscriber.SubscriberInterface;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCP-based subscriber implementation that connects to a rate provider over socket,
 * subscribes to rates, and listens for incoming data in a separate thread.
 * <p>
 * Communicates with a central {@link CoordinatorInterface} to report connection events and rate updates.
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
     * Constructs a {@code TCPSubscriber} instance with the specified name, server address and port.
     *
     * @param subscriberName the name of this subscriber (e.g., "PF1")
     * @param serverAddress  the IP or hostname of the TCP server
     * @param serverPort     the port number to connect to
     * @throws IOException if socket setup fails
     */
    public TCPSubscriber(String subscriberName, String serverAddress, int serverPort) throws IOException {
        logger.info("Initializing {} Subscriber", subscriberName);
        this.subscriberName = subscriberName;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.subscribedRateList = new ArrayList<>();
        logger.info("{} Subscriber Initialized", subscriberName);
    }

    /**
     * Assigns the coordinator reference for sending lifecycle and rate events.
     */
    @Override
    public void setCoordinator(CoordinatorInterface coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * Connects to the TCP server and performs login handshake.
     *
     * @param platformName name of the platform
     * @param userName     login username
     * @param password     login password
     * @throws IOException if connection or handshake fails
     */
    @Override
    public void connect(String platformName, String userName, String password) throws IOException {
        logger.info("Connecting to {}:{}", serverAddress, serverPort);
        this.connectionSocket = new Socket(serverAddress, serverPort);
        this.reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.writer = new PrintWriter(connectionSocket.getOutputStream(), true);

        writer.println(userName + "|" + password);
        this.connectionStatus = connectionSocket.isConnected();

        if (!this.connectionStatus) {
            connectionSocket.close();
            System.out.println("Connection closed - wrong credentials.");
            return;
        }

        logger.info("{} Connected to {}:{}", subscriberName, serverAddress, serverPort);
        coordinator.onConnect(platformName, connectionStatus);
        this.start();
    }

    /**
     * Gracefully disconnects from the TCP server and notifies the coordinator.
     */
    @Override
    public void disConnect(String platformName, String userid, String password) throws IOException {
        logger.info("Disconnecting from {}", serverAddress);
        this.writer.close();
        this.reader.close();
        this.connectionSocket.close();
        this.connectionStatus = false;
        coordinator.onDisConnect(platformName, connectionStatus);
        logger.info("{} Disconnected from {}", subscriberName, serverAddress);
    }

    /**
     * Sends a subscription request for a specific rate to the TCP server.
     */
    @Override
    public void subscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is subscribing to {}", platformName, rateName);
        this.writer.println("subscribe|" + platformName + "_" + rateName);
        this.subscribedRateList.add(rateName);
        logger.info("{} subscribed to {}", platformName, rateName);
    }

    /**
     * Sends an unsubscription request for a specific rate to the TCP server.
     */
    @Override
    public void unSubscribe(String platformName, String rateName) throws IOException {
        logger.info("{} is unsubscribing from {}", platformName, rateName);
        this.writer.println("unsubscribe|" + platformName + "_" + rateName);
        this.subscribedRateList.remove(rateName);
        logger.info("{} unsubscribed from {}", platformName, rateName);
    }

    /**
     * Continuously listens for incoming rate data from the server and
     * notifies the coordinator of any available or updated rates.
     */
    @Override
    public void run() {
        String response;
        while (connectionStatus) {
            try {
                response = reader.readLine();
                RateDto rateDto = RateMapper.stringToRateDto(response);

                for (String subscribedRateName : subscribedRateList) {
                    if (rateDto.getRateName().equals(this.subscriberName + "_" + subscribedRateName)) {
                        switch (coordinator.onRateStatus(this.subscriberName, rateDto.getRateName())) {
                            case RateStatus.NOT_AVAILABLE:
                                coordinator.onRateAvailable(this.subscriberName, rateDto.getRateName(), rateDto);
                                break;
                            case RateStatus.AVAILABLE:
                            case RateStatus.UPDATED:
                                coordinator.onRateUpdate(this.subscriberName, rateDto.getRateName(), rateDto);
                                break;
                        }
                    }
                }

            } catch (IOException e) {
                logger.error("{} Subscriber Error: {}", subscriberName, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
