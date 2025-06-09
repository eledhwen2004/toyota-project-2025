package com.main.ClientConnection;

import com.main.Rate.Rate;
import com.main.TempUser.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the connection between the server and a client.
 * <p>
 * Each {@code ClientConnection} manages a separate socket for a connected client,
 * processes subscription/unsubscription requests, validates user credentials, and
 * periodically sends rate updates to the client.
 * </p>
 */
public class ClientConnection extends Thread implements ClientConnectionInterface {

    private final Socket clientSocket;
    private final List<Rate> rateList;
    private final List<Rate> subscribedRateList;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Thread rateSender;
    private final User exampleUser;

    /**
     * Returns the socket associated with this client connection.
     *
     * @return the client's socket
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Initializes a new {@code ClientConnection}, performs user authentication,
     * and prepares streams and threads for communication.
     *
     * @param clientSocket the socket connected to the client
     * @param rateList     the list of available rates to subscribe
     * @throws IOException if an I/O error occurs while setting up the connection
     */
    public ClientConnection(Socket clientSocket, List<Rate> rateList) throws IOException {
        this.exampleUser = new User("1234", "1234");
        this.rateList = rateList;
        this.subscribedRateList = new ArrayList<>();
        this.clientSocket = clientSocket;

        this.rateSender = new Thread(() -> {
            try {
                this.sendSubscribedRates();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);

        String connectionMessage = reader.readLine();
        if (connectionMessage == null) {
            this.clientSocket.close();
            return;
        }

        String[] user = connectionMessage.split("\\|");
        if (!user[0].equals(exampleUser.getUsername()) || !user[1].equals(exampleUser.getPassword())) {
            writer.println("Wrong username or password connection closing");
            System.out.println("Connection closing...");
            this.clientSocket.close();
            return;
        }

        this.start();
    }

    /**
     * Parses and handles incoming client messages.
     * Supported messages: {@code subscribe|rateName} or {@code unsubscribe|rateName}
     *
     * @param message the received message from the client
     */
    @Override
    public void handleMessageTaken(String message) {
        String[] messageParts = message.split("\\|");
        if (messageParts.length != 2) {
            writer.println("Invalid message received: " + message);
            return;
        }

        switch (messageParts[0]) {
            case "subscribe":
                subscribeToRate(messageParts[1]);
                break;
            case "unsubscribe":
                unsubscribeFromRate(messageParts[1]);
                break;
            default:
                writer.println("Invalid message received: " + message);
                break;
        }
    }

    /**
     * Subscribes the client to updates for a specific rate.
     *
     * @param rateName the name of the rate to subscribe to
     */
    @Override
    public void subscribeToRate(String rateName) {
        for (Rate rate : rateList) {
            if (rate.getRateName().equals(rateName)) {
                synchronized (subscribedRateList) {
                    subscribedRateList.add(rate);
                }
            }
        }
    }

    /**
     * Unsubscribes the client from a previously subscribed rate.
     *
     * @param rateName the name of the rate to unsubscribe from
     */
    @Override
    public void unsubscribeFromRate(String rateName) {
        for (Rate rate : subscribedRateList) {
            if (rate.getRateName().equals(rateName)) {
                synchronized (subscribedRateList) {
                    subscribedRateList.remove(rate);
                }
            }
        }
    }

    /**
     * Continuously sends the current values of subscribed rates to the client at regular intervals (1 second).
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    @Override
    public void sendSubscribedRates() throws InterruptedException {
        do {
            synchronized (subscribedRateList) {
                for (Rate rate : subscribedRateList) {
                    writer.println(rate.getRateName() + "|" + rate.getAsk() + "|" + rate.getBid() + "|" + rate.getTimestamp());
                }
            }
            Thread.sleep(1000);
        } while (clientSocket.isConnected());
    }

    /**
     * Starts the communication thread. Listens for client messages and processes them accordingly.
     */
    @Override
    public void run() {
        rateSender.start();
        String messageTaken;
        do {
            try {
                handleMessageTaken(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (clientSocket.isConnected());

        System.out.println("Connection closing...");
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
