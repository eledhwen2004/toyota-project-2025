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
 * The server that manages client connections, rate updates, and server lifecycle.
 * It accepts client connections, processes rate updates, and ensures proper cleanup
 * of disconnected clients.
 */
public class Server extends Thread implements ServerInterface {

    private boolean serverStatus = true;
    private final ServerSocket serverSocket;
    private final Thread disconnectedConnectionRemover;
    private List<ClientConnection> clientConnectionList;
    private List<Rate> rateList;

    /**
     * Gets the status of the server (whether it is running or stopped).
     *
     * @return true if the server is running, false if stopped.
     */
    public boolean getServerStatus() {
        return this.serverStatus;
    }

    /**
     * Constructor that initializes the server, creates the server socket,
     * initializes the rate list, and starts rate threads.
     *
     * @throws IOException if there is an error initializing the server or rates.
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

        // Initialize rate list from the configuration
        for (String rateName : Configuration.getRateNames()) {
            rateList.add(new Rate(this, rateName, Configuration.getFirstRateAsk(rateName), Configuration.getFirstRateBid(rateName)));
        }

        // Start all rate threads
        for (Rate rate : rateList) {
            rate.start();
            System.out.println("Rate started: " + rate.getRateName());
        }

        this.serverStatus = true;
    }

    /**
     * Runs the server, accepting client connections and handling rate updates.
     * It runs until the server is stopped.
     */
    @Override
    public void run() {
        disconnectedConnectionRemover.start();
        System.out.println("Server started");

        do {
            try {
                this.acceptClientConnection(serverSocket.accept());
                System.out.println("Client connected!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (serverStatus);

        System.out.println("Server is closing");

        try {
            closeAllClientConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Server is closed");
    }

    /**
     * Stops the server by setting the server status to false.
     */
    @Override
    public void stopServer() {
        this.serverStatus = false;
    }

    /**
     * Accepts a new client connection and adds it to the client connection list.
     *
     * @param newConnection the new client socket connection.
     * @throws IOException if there is an error adding the client connection.
     */
    @Override
    public void acceptClientConnection(Socket newConnection) throws IOException {
        clientConnectionList.add(new ClientConnection(newConnection, rateList));
    }

    /**
     * Closes the specified client connection.
     *
     * @param clientConnection the client connection to close.
     * @throws IOException if there is an error closing the client connection.
     */
    @Override
    public void closeClientConnection(ClientConnection clientConnection) throws IOException {
        clientConnection.getClientSocket().close();
    }

    /**
     * Closes all client connections and removes them from the list.
     *
     * @throws IOException if there is an error closing the client connections.
     */
    @Override
    public void closeAllClientConnections() throws IOException {
        for (ClientConnection clientConnection : clientConnectionList) {
            clientConnection.getClientSocket().close();
            clientConnectionList.remove(clientConnection);
        }
    }

    /**
     * Periodically checks for disconnected client connections and closes them.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping.
     * @throws IOException if there is an error closing a disconnected client connection.
     */
    @Override
    public void closeDisconnectedClientConnections() throws InterruptedException, IOException {
        do {
            for (ClientConnection clientConnection : this.clientConnectionList) {
                if (!clientConnection.getClientSocket().isConnected()) {
                    clientConnection.getClientSocket().close();
                    this.clientConnectionList.remove(clientConnection);
                }
            }
            Thread.sleep(2500); // Wait 2.5 seconds before checking again
        } while (serverStatus);
    }
}
