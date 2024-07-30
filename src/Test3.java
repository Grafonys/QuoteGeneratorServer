import QuoteGeneratorServer.Client;

public class Test3 {

    public static void main(String[] args) {
        Client client = new Client("Gr", "123", "127.0.0.1", 9000);
        client.start();
    }
}
