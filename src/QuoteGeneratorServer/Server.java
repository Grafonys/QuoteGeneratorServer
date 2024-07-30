package QuoteGeneratorServer;

import Threads.ClientAccepter;
import Threads.ServerCloseListener;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final int MAX_USERS = 2;
    public static final int MAX_QUOTES = 3;
    public static final String LINE = "-".repeat(100);

    private final String host;
    private final int port;
    private ServerSocket serverSocket;
    private final String[] loginData;
    private final List<String> activeUsers;
    private final RandomQuote randomQuote;
    private final ServerLogger logger;
    private volatile boolean isClosed;




    public static void main(String[] args) {
        Server server = new Server("127.0.0.1", 9000);
        server.start();
    }


    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        loginData = new String[]{"Graf 123", "Leon 123", "Carl 321"};
        activeUsers = new ArrayList<>();
        randomQuote = new RandomQuote("StathamQuotes.txt", "---");
        logger = new ServerLogger();
        isClosed = false;
    }

    public void start() {
        serverInit();
        String now = ServerLogger.getCurrentDateTime();
        logger.log(now);
        logger.log(LINE);

        ServerCloseListener closeListener = new ServerCloseListener(this);
        closeListener.start();

        ClientAccepter clientAccepter = new ClientAccepter(this);
        clientAccepter.start();

        while (!isClosed) {
            Thread.onSpinWait();
        }

        try {
            closeListener.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.log(LINE);
        now = ServerLogger.getCurrentDateTime();
        logger.log(now);
        logger.separate();
        closeServer();
    }

    private void serverInit() {
        try {
            serverSocket = new ServerSocket(port, MAX_USERS, InetAddress.getByName(host));
        } catch (IOException e) {
            System.err.println("SERVER INITIALIZATION EXCEPTION");
            throw new RuntimeException(e);
        }
    }

    private void closeServer() {
        try {
            logger.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("SERVER CLOSING EXCEPTION");
            throw new RuntimeException(e);
        }
    }

    public BufferedReader inInit(Socket client) {
        try {
            return new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            System.err.println("SERVER INPUT INITIALIZATION EXCEPTION");
            throw new RuntimeException(e);
        }
    }

    public PrintWriter outInit(Socket client) {
        try {
            return new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("SERVER OUTPUT INITIALIZATION EXCEPTION");
            throw new RuntimeException(e);
        }
    }

    public Socket connectClient() {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            return null;
        }
    }

    public void disconnectClient(Socket client, Reader in, Writer out) {
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean authentication(String loginPassword) {
        for (String userData : loginData) {
            if (userData.equals(loginPassword)) return true;
        }
        return false;
    }

    public boolean isUserActive(String loginPassword) {
        for (String userData : activeUsers) {
            if (userData.equals(loginPassword)) { return true; }
        }
        return false;
    }

    public void addActiveUser(String loginPassword) {
        activeUsers.add(loginPassword);
    }

    public void removeActiveUser(String loginPassword) {
        for (int i = 0; i < activeUsers.size(); i++) {
            if (activeUsers.get(i).equals(loginPassword)) {
                activeUsers.remove(i);
                break;
            }
        }
    }

    public void sendMessage(PrintWriter out, String message) {
        out.println(message);
    }

    public String receiveMessage(BufferedReader in) {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.err.println("SERVER RECEIVING MESSAGE EXCEPTION");
            throw new RuntimeException(e);
        }
    }

    public void close() {
        isClosed = true;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public int getActiveUsersCount() {
        return activeUsers.size();
    }

    public String getRandomQuote() {
        return randomQuote.nextQuote();
    }

    public ServerLogger getLogger() {
        return logger;
    }
}