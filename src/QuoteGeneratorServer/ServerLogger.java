package QuoteGeneratorServer;

import java.io.*;
import java.time.LocalDateTime;


public class ServerLogger {
    private final PrintWriter out;

    public ServerLogger() {
        try {
            out = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("serverLog.txt", true)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%d %s %d  -  %d:%d:%d",
                now.getDayOfMonth(),
                now.getMonth().name(),
                now.getYear(),
                now.getHour(),
                now.getMinute(),
                now.getSecond());
    }

    public void log(String message) {
        out.println(message);
    }

    public void separate() {
        log("\n\n");
    }

    public void close() {
        out.close();
    }
}
