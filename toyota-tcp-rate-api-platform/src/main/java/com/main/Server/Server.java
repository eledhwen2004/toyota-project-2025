package com.main.Server;

import com.main.ClientConnection.ClientConnection;
import com.main.Configuration.Configuration;
import com.main.Rate.Rate;
import com.main.TempUser.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread implements ServerInterface {
    private boolean serverStatus = true;
    private final ServerSocket serverSocket;
    private final Thread disconnectedConnectionRemover;
    private List<ClientConnection> clientConnectionList;
    private List<Rate>rateList;

    public boolean getServerStatus() {
        return this.serverStatus;
    }


    public Server() throws IOException {
        this.disconnectedConnectionRemover = new Thread(()-> {
            try {
                this.closeDisconnectedClientConnections();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.serverSocket = new ServerSocket(8081);
        this.clientConnectionList = new ArrayList<>();
        this.rateList = new ArrayList<>();
        for(String rateName: Configuration.getRateNames()) {
            rateList.add(new Rate(this,rateName,Configuration.getFirstRateAsk(rateName),Configuration.getFirstRateBid(rateName)));
        }
        for(Rate rate:rateList) {
            rate.start();
            System.out.println("Rate started: " + rate.getRateName());
        }
        this.serverStatus = true;

    }

    @Override
    public void run() {
        disconnectedConnectionRemover.start();
        System.out.println("Server started");
        do{
            try {
                this.acceptClientConnection(serverSocket.accept());
                System.out.println("Client connected!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }while(serverStatus);

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

    @Override
    public void stopServer() {
        this.serverStatus = false;
    }

    @Override
    public void acceptClientConnection(Socket newConnection) throws IOException {
        clientConnectionList.add(new ClientConnection(newConnection,rateList));
    }

    @Override
    public void closeClientConnection(ClientConnection clientConnection) throws IOException {
        clientConnection.getClientSocket().close();
    }

    @Override
    public void closeAllClientConnections() throws IOException {
        for(ClientConnection clientConnection:clientConnectionList){
            clientConnection.getClientSocket().close();
            clientConnectionList.remove(clientConnection);
        }
    }

    @Override
    public void closeDisconnectedClientConnections() throws InterruptedException, IOException {
        do{
            for(ClientConnection clientConnection:this.clientConnectionList) {
                if (!clientConnection.getClientSocket().isConnected()) {
                    clientConnection.getClientSocket().close();
                    this.clientConnectionList.remove(clientConnection);
                }
            }
            Thread.sleep(2500);
        }while(serverStatus);
    }
}
