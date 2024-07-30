package Threads;

import QuoteGeneratorServer.Server;
import java.util.Scanner;


public class ServerCloseListener extends Thread {
    private final Server server;

    public ServerCloseListener(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        String input = "";

        while (!input.equals("close")) {
            input = in.nextLine();
        }
        in.close();
        server.close();
    }
}