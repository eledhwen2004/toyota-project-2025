package com.main.ClientConnection;

import com.main.Rate.Rate;
import com.main.User.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client connection to the server. This class handles the communication between the server
 * and a single client, allowing the client to subscribe/unsubscribe to rate updates and receive those updates.
 */
public class ClientConnection extends Thread implements ClientConnectionInterface {

    /**
     * The socket that represents the connection between the client and the server.
     */
    private final Socket clientSocket;

    /**
     * A list of all available rates.
     */
    private final List<Rate> rateList;

    /**
     * A list of rates the client has subscribed to.
     */
    private final List<Rate> subscribedRateList;

    /**
     * BufferedReader for reading input from the client.
     */
    private final BufferedReader reader;

    /**
     * PrintWriter for sending output to the client.
     */
    private final PrintWriter writer;

    /**
     * The thread responsible for sending subscribed rates to the client.
     */
    private final Thread rateSender;

    /**
     * A temporary user object used for authentication.
     */
    private final User exampleUser;

    /**
     * Retrieves the client socket for the current connection.
     *
     * @return the client socket.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Initializes a new {@link ClientConnection} object, setting up the necessary streams, user authentication,
     * and starting the rate sender thread.
     *
     * @param clientSocket the socket representing the client connection.
     * @param rateList the list of available rates.
     * @throws IOException if an error occurs while setting up the input/output streams or during authentication.
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
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Authentication
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
        //
        this.start();
    }

    /**
     * Handles incoming messages from the client. It processes messages to either subscribe or unsubscribe
     * from a rate update.
     *
     * @param message the incoming message from the client.
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
     * Subscribes the client to receive updates for a specified rate.
     *
     * @param rateName the name of the rate to subscribe to.
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
     * Unsubscribes the client from receiving updates for a specified rate.
     *
     * @param rateName the name of the rate to unsubscribe from.
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
     * Sends the updates of the subscribed rates to the client at a regular interval.
     * The updates are sent every second.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping.
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
     * Starts the rate sender thread and continuously listens for incoming messages from the client.
     * When the connection is closed, the client socket is also closed.
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
