package Threads;

import QuoteGeneratorServer.Server;

import java.net.Socket;

public class ClientAccepter extends Thread {
    private final Server server;

    public ClientAccepter(Server server) {
        this.server = server;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!server.isClosed()) {
            Socket client = server.connectClient();
            if (client != null) {
                ClientHandler clientHandler = new ClientHandler(client, server);
                clientHandler.start();
            }
        }
    }
}
