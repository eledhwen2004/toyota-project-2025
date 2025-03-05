package com.main;

import com.main.Server.Server;

import java.io.IOException;

public class ToyotaTcpRateApiPlatformApplication {

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }

}
