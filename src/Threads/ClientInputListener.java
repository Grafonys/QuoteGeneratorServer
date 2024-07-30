package Threads;

import QuoteGeneratorServer.Client;

public class ClientInputListener extends Thread {

    private final Client client;

    public ClientInputListener(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        String receivedMessage;

        do {
            receivedMessage = client.receiveMessage();

            if (receivedMessage == null) {
                System.err.println("Соединение с сервером разорвано!");
            } else {
                System.out.println(receivedMessage);
            }
        } while (client.checkConnection(receivedMessage));
        System.out.println("Нажмите Enter для завершения");
    }
}