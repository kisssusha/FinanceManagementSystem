import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    private static final Map<String, Double> ratesToRUB = new HashMap<>();

    static {
        ratesToRUB.put("USD", 0.00975);
        ratesToRUB.put("EUR", 0.00949);
        ratesToRUB.put("RUB", 1.0);
    }

    public static double convert(String fromCurrency, String toCurrency, double amount) {

        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        updateRates();

        if (!ratesToRUB.containsKey(fromCurrency) || !ratesToRUB.containsKey(toCurrency)) {
            StringBuilder unknownCurrencies = new StringBuilder();
            if (!ratesToRUB.containsKey(fromCurrency)) {
                unknownCurrencies.append("Unknown currency: ").append(fromCurrency);
            }
            if (!ratesToRUB.containsKey(toCurrency)) {
                if (!unknownCurrencies.isEmpty()) {
                    unknownCurrencies.append("; ");
                }
                unknownCurrencies.append("Unknown currency: ").append(toCurrency);
            }
            throw new IllegalArgumentException(unknownCurrencies.toString());
        }

        double amountInRUB = amount * ratesToRUB.get(fromCurrency);
        return amountInRUB * ratesToRUB.get(toCurrency);
    }

    public static void updateRates() {
        String urlString = "https://api.exchangerate-api.com/v4/latest/RUB";
        try {
            URI uri = URI.create(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                parseRates(response.toString());
                System.out.println("Currency rates updated.");
            } else {
                System.out.println("Error updating rates. Response code: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println("Error requesting currency rates: " + e.getMessage());
        }
    }

    private static void parseRates(String json) {
        String ratesSection = json.split("\"rates\":\\{")[1].split("}")[0];
        String[] pairs = ratesSection.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.replaceAll("[\"}]", "").split(":");
            if (keyValue.length == 2) {
                String currency = keyValue[0].trim();
                try {
                    double rate = Double.parseDouble(keyValue[1].trim());
                    ratesToRUB.put(currency, rate);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }
}
