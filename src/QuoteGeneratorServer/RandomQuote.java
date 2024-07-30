package QuoteGeneratorServer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomQuote {
    private final String fileName;
    private final String separator;
    private List<String> quotes;

    RandomQuote(String fileName, String separator) {
        this.fileName = fileName;
        this.separator = separator;
        formQuoteList();
    }

    public String nextQuote() {
        Random random = new Random();
        int i = random.nextInt(quotes.size());
        return quotes.get(i);
    }

    private void formQuoteList() {
        try (BufferedReader fileReader = fileReaderInit()) {
            quotes = new ArrayList<>();
            StringBuilder quote = new StringBuilder("");
            String stroke = fileReader.readLine();

            while (stroke != null) {
                if (stroke.equals(separator)) {
                    quotes.add(quote.toString());
                    quote = new StringBuilder("");
                } else {
                    quote.append(stroke);
                }
                stroke = fileReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader fileReaderInit() {
        try {
            return new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}