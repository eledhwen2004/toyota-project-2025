package com.main.Server;

import com.main.ClientConnection.ClientConnection;

import java.io.IOException;
import java.net.Socket;

/**
 * Defines the contract for a rate broadcasting TCP server.
 * <p>
 * Implementing classes are responsible for accepting client connections,
 * managing connection lifecycles, and ensuring that disconnected clients are properly cleaned up.
 */
public interface ServerInterface {

    /**
     * Stops the server and all of its associated activities.
     * <p>
     * Implementations should ensure that no new clients can connect and that the server loop terminates cleanly.
     */
    void stopServer();

    /**
     * Accepts and handles a new client connection.
     *
     * @param newClientConnection the newly accepted {@link Socket} from a client
     * @throws IOException if an error occurs during connection setup
     */
    void acceptClientConnection(Socket newClientConnection) throws IOException;

    /**
     * Closes the specified client connection gracefully.
     *
     * @param clientConnection the client connection to be closed
     * @throws IOException if an error occurs while closing the socket
     */
    void closeClientConnection(ClientConnection clientConnection) throws IOException;

    /**
     * Closes all currently active client connections.
     *
     * @throws IOException if an error occurs while closing one or more client sockets
     */
    void closeAllClientConnections() throws IOException;

    /**
     * Periodically checks for disconnected clients and removes them from the server’s connection list.
     *
     * @throws InterruptedException if the cleanup thread is interrupted during sleep
     * @throws IOException          if closing a disconnected socket fails
     */
    void closeDisconnectedClientConnections() throws InterruptedException, IOException;
}
