package com.main.Server;

import com.main.ClientConnection.ClientConnection;

import java.io.IOException;
import java.net.Socket;

public interface ServerInterface {
    void stopServer();
    void acceptClientConnection(Socket newClientConnection) throws IOException;
    void closeClientConnection(ClientConnection clientConnection) throws IOException;
    void closeAllClientConnections() throws IOException;
    void closeDisconnectedClientConnections() throws InterruptedException, IOException;
}
