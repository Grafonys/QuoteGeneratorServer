package Threads;

import QuoteGeneratorServer.Server;
import QuoteGeneratorServer.ServerLogger;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket client;
    private final Server server;
    private final ServerLogger logger;
    private StringBuilder log;
    private final BufferedReader in;
    private final PrintWriter out;

    public ClientHandler(Socket client, Server server) {
        this.client = client;
        this.server = server;
        logger = server.getLogger();
        this.in = server.inInit(client);
        this.out = server.outInit(client);
    }

    @Override
    public void run() {
        log = new StringBuilder("---Подключение: " + ServerLogger.getCurrentDateTime());
        String loginPassword = server.receiveMessage(in);
        log.append("\n").append(loginPassword);

        if (server.getActiveUsersCount() >= Server.MAX_USERS) {
            server.sendMessage(out, """
                        В данный момент сервер находится под максимальной нагрузкой.
                        Попробуйте подключиться через некоторое время.
                        """);
            log.append("\nСервер заполнен.");
        } else {
            server.sendMessage(out, """
                        Добро пожаловать на сервер с цитатами Джейсона Стэтхэма.
                        Проверка логина и пароля...
                        """);
            boolean isSuccess = server.authentication(loginPassword);
            boolean isActive = server.isUserActive(loginPassword);

            if (isSuccess) {
                if (isActive) {
                    server.sendMessage(out, "Пользователь с вашими данными уже находится на сервере!");
                    log.append("\nТакой пользователь уже на сервере.");
                } else {
                    server.addActiveUser(loginPassword);
                    interactionWithClient();
                }
            } else {
                server.sendMessage(out, "Неверное имя пользователя или пароль!");
                log.append("\nНе пройдена аутентификация.");
            }
        }
        server.sendMessage(out, "Идет отключение...");
        server.disconnectClient(client, in, out);
        log.append("\n\n-Отключение: ").append(ServerLogger.getCurrentDateTime());
        logger.log(log.toString());
        logger.separate();
        server.removeActiveUser(loginPassword);
    }

    private void interactionWithClient() {
        server.sendMessage(out, String.format("""
                            Авторизация прошла успешно!
                            Доступное количество цитат: %d
                            Для получения случайной цитаты введите 1
                            Для выхода введите 2
                            """, Server.MAX_QUOTES));

        int quoteCount = 0;
        String receivedMessage;

        do {
            if (server.isClosed()) {
                server.sendMessage(out, "Сервер завершил свою работу!");
                break;
            }

            server.sendMessage(out, Server.LINE);
            String randomQuote = server.getRandomQuote();
            server.sendMessage(out, randomQuote);
            log.append("\n\n").append(randomQuote);
            server.sendMessage(out, Server.LINE);
            quoteCount++;

            if (quoteCount == Server.MAX_QUOTES) {
                server.sendMessage(out, "Достигнуто максимальное количество цитат.");
                break;
            }
            receivedMessage = server.receiveMessage(in);
        } while (!receivedMessage.equals("2"));
    }
}