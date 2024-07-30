import QuoteGeneratorServer.Client;

public class Test1 {

    public static void main(String[] args) {
        Client client = new Client("Graf", "123", "127.0.0.1", 9000);
        client.start();
    }
}
