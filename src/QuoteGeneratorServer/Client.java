package QuoteGeneratorServer;

import Threads.ClientInputListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private Socket clientSocket;
    private boolean isConnected;
    private Scanner scanner;
    private BufferedReader in;
    private PrintWriter out;


    public Client(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        clientSocket = null;
    }

    public void start() {
        connect();

        if (isConnected) {
            ClientInputListener inputListener = new ClientInputListener(this);
            inputListener.start();

            login();

            while (isConnected) {
                sendMessage();
            }

            try {
                inputListener.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        disconnect();
    }

    private void connect() {
        try {
            clientSocket = new Socket(host, port);
            isConnected = true;
            inOutInit();
        } catch (IOException e) {
            System.err.println("Сервер закрыт!");
            isConnected = false;
        }
    }

    public boolean checkConnection(String receivedMessage) {
        isConnected = receivedMessage != null && !(receivedMessage.equals("Идет отключение..."));
        return isConnected;
    }

    private void disconnect() {
        try {
            if (clientSocket != null) {
                scanner.close();
                in.close();
                out.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("CLIENT DISCONNECTION EXCEPTION");
            throw new RuntimeException(e);
        }
    }

    private void inOutInit() {
        try {
            scanner = new Scanner(System.in);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("CLIENT I/O INITIALIZATION EXCEPTION");
            throw new RuntimeException(e);
        }
    }

    public String receiveMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void sendMessage() {
        String message = scanner.nextLine();
        out.println(message);
    }

    private void login() {
        out.println(username + " " + password);
    }
}