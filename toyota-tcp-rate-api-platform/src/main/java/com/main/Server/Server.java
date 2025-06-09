package com.main.Server;

import com.main.ClientConnection.ClientConnection;
import com.main.Configuration.Configuration;
import com.main.Rate.Rate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCP-based server implementation that handles real-time rate data and client connections.
 * <p>
 * The server spawns {@link Rate} threads based on configuration and allows clients to connect
 * through sockets. It also maintains a list of connected clients and cleans up disconnected ones.
 */
public class Server extends Thread implements ServerInterface {

    private boolean serverStatus = true;
    private final ServerSocket serverSocket;
    private final Thread disconnectedConnectionRemover;
    private List<ClientConnection> clientConnectionList;
    private List<Rate> rateList;

    /**
     * Returns the current operational status of the server.
     *
     * @return {@code true} if the server is running, otherwise {@code false}
     */
    public boolean getServerStatus() {
        return this.serverStatus;
    }

    /**
     * Initializes the server by loading rate configurations, creating rate threads,
     * and preparing the socket server for incoming client connections.
     *
     * @throws IOException if socket creation or configuration loading fails
     */
    public Server() throws IOException {
        this.disconnectedConnectionRemover = new Thread(() -> {
            try {
                this.closeDisconnectedClientConnections();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.serverSocket = new ServerSocket(8081);
        this.clientConnectionList = new ArrayList<>();
        this.rateList = new ArrayList<>();

        for (String rateName : Configuration.getRateNames()) {
            rateList.add(new Rate(this, rateName,
                    Configuration.getFirstRateAsk(rateName),
                    Configuration.getFirstRateBid(rateName)));
        }

        for (Rate rate : rateList) {
            rate.start();
            System.out.println("Rate started: " + rate.getRateName());
        }

        this.serverStatus = true;
    }

    /**
     * Starts the server loop which accepts new client connections
     * and monitors for disconnections.
     */
    @Override
    public void run() {
        disconnectedConnectionRemover.start();
        System.out.println("Server started");

        while (serverStatus) {
            try {
                this.acceptClientConnection(serverSocket.accept());
                System.out.println("Client connected!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Server is closing");

        try {
            closeAllClientConnections();
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Server is closed");
    }

    /**
     * Stops the server loop.
     */
    @Override
    public void stopServer() {
        this.serverStatus = false;
    }

    /**
     * Accepts a new client connection and initializes a {@link ClientConnection} instance.
     *
     * @param newConnection the new socket connection
     * @throws IOException if connection setup fails
     */
    @Override
    public void acceptClientConnection(Socket newConnection) throws IOException {
        clientConnectionList.add(new ClientConnection(newConnection, rateList));
    }

    /**
     * Closes a specific client connection.
     *
     * @param clientConnection the connection to be closed
     * @throws IOException if closing the socket fails
     */
    @Override
    public void closeClientConnection(ClientConnection clientConnection) throws IOException {
        clientConnection.getClientSocket().close();
    }

    /**
     * Closes all active client connections.
     *
     * @throws IOException if closing any client socket fails
     */
    @Override
    public void closeAllClientConnections() throws IOException {
        for (ClientConnection clientConnection : clientConnectionList) {
            clientConnection.getClientSocket().close();
        }
        clientConnectionList.clear();
    }

    /**
     * Periodically checks for disconnected clients and removes them from the list.
     *
     * @throws InterruptedException if the thread sleep is interrupted
     * @throws IOException          if closing a socket fails
     */
    @Override
    public void closeDisconnectedClientConnections() throws InterruptedException, IOException {
        while (serverStatus) {
            for (ClientConnection clientConnection : new ArrayList<>(clientConnectionList)) {
                if (!clientConnection.getClientSocket().isConnected()) {
                    clientConnection.getClientSocket().close();
                    clientConnectionList.remove(clientConnection);
                }
            }
            Thread.sleep(2500);
        }
    }
}
