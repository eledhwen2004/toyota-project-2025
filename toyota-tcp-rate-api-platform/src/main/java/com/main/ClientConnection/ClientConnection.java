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
import java.util.Objects;
import java.util.regex.Pattern;

public class ClientConnection extends Thread implements ClientConnectionInterface{

    private final Socket clientSocket;
    private final List<Rate> rateList;
    private final List<Rate> subscribedRateList;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Thread rateSender;
    private final User exampleUser;


    public Socket getClientSocket() {
        return clientSocket;
    }

    public ClientConnection(Socket clientSocket,List<Rate> rateList) throws IOException {
        this.exampleUser = new User("1234","1234");
        this.rateList = rateList;
        this.subscribedRateList = new ArrayList<>();
        this.clientSocket = clientSocket;
        this.rateSender = new Thread(()-> {
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
        // Here will change
        String connectionMessage = reader.readLine();
        if (connectionMessage == null) {
            this.clientSocket.close();
            return;
        }
        String []user = connectionMessage.split("\\|");
        if(!user[0].equals(exampleUser.getUsername()) || !user[1].equals(exampleUser.getPassword())){
            writer.println("Wrong username or password connection closing");
            System.out.println("Connection closing...");
            this.clientSocket.close();
            return;
        }
        //
        this.start();
    }

    @Override
    public void handleMessageTaken(String message) {
        String []messageParts = message.split("\\|");
        if(messageParts.length != 2) {
            writer.println("Invalid message received: " + message);
            return;
        }
        switch (messageParts[0]){
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

    @Override
    public void subscribeToRate(String rateName) {
        for(Rate rate: rateList) {
            if(rate.getRateName().equals(rateName)) {
                synchronized (subscribedRateList) {
                    subscribedRateList.add(rate);
                }
            }
        }
    }

    @Override
    public void unsubscribeFromRate(String rateName) {
        for(Rate rate: subscribedRateList) {
            if(rate.getRateName().equals(rateName)) {
                synchronized (subscribedRateList) {
                    subscribedRateList.remove(rate);
                }
            }
        }
    }

    @Override
    public void sendSubscribedRates() throws InterruptedException {
        do{
            synchronized(subscribedRateList) {
                for(Rate rate: subscribedRateList){
                    writer.println(rate.getRateName()+"|"+rate.getAsk()+"|"+rate.getBid()+"|"+rate.getTimestamp());
                }
            }
            Thread.sleep(1000);
        }while(clientSocket.isConnected());
    }


    @Override
    public void run() {
        rateSender.start();
        String messageTaken;
        do{
            try {
                handleMessageTaken(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }while(clientSocket.isConnected());

        System.out.println("Connection closing...");
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
