package cz.cvut.fit.bap.parser.controller.currency_exchanger;

import io.micrometer.core.annotation.Timed;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Exchanges currencies with <a href="https://fixer.io/">fixer.io</a>
 */
@Component
public class CurrencyExchanger implements ICurrencyExchanger {
    private final String fixerApiKey;
    private static final String BASE_URL = "http://data.fixer.io/api/";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyExchanger.class);

    public CurrencyExchanger(@Value("${FIXER_API_KEY}") String fixerApiKey) {
        this.fixerApiKey = fixerApiKey;
    }

    @Override
    @Timed(value = "scrapper.currency.exchange")
    public Optional<BigDecimal> exchange(BigDecimal value, Currency from, Currency to, LocalDate date) {
        String urlString = buildApiUrl(from, to, date);
        try {
            HttpResponse<String> response = sendHttpRequest(urlString);
            LOGGER.info("Exchanging currency from {} to {}.", from.name(), to.name());
            if (!responseIsValid(response)) {
                return Optional.empty();
            }

            JSONObject jsonObject = new JSONObject(response.body());

            JSONObject rates = jsonObject.getJSONObject("rates");

            BigDecimal rateFrom = getExchangeRate(from, rates);
            BigDecimal rateTo = getExchangeRate(to, rates);

            // If converting from a non-EUR currency, first convert the value to EUR
            BigDecimal valueInEur = convertToEur(value, from, rateFrom);

            // Convert the EUR value to the target currency
            return convertToTargetCurrency(valueInEur, rateTo);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private boolean responseIsValid(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            LOGGER.debug("Exchanging currency failed with status code {}.", response.statusCode());
            return false;
        }
        JSONObject jsonObject = new JSONObject(response.body());
        boolean success = jsonObject.getBoolean("success");
        if (!success) {
            JSONObject errorObject = jsonObject.getJSONObject("error");
            String type = errorObject.getString("type");
            LOGGER.debug("Exchange failed with error type: {}", type);
            if (type.equals("invalid_access_key")) {
                throw new RuntimeException("Invalid api key for fixer.io");
            }
            return false;
        }
        return true;
    }

    private BigDecimal getExchangeRate(Currency currency, JSONObject rates) {
        return currency.equals(Currency.EUR) ? BigDecimal.ONE : rates.getBigDecimal(currency.name());
    }

    private String buildApiUrl(Currency from, Currency to, LocalDate date) {
        return BASE_URL + date
                + "?access_key=" + fixerApiKey
                + "&symbols=" + from + "," + to;
    }

    private HttpResponse<String> sendHttpRequest(String urlString) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private BigDecimal convertToEur(BigDecimal value, Currency from, BigDecimal rateFrom) {
        return from.equals(Currency.EUR) ? value : value.divide(rateFrom, 7, RoundingMode.HALF_UP);
    }

    private Optional<BigDecimal> convertToTargetCurrency(BigDecimal valueInEur, BigDecimal rateTo) {
        BigDecimal result = valueInEur.multiply(rateTo).setScale(2, RoundingMode.HALF_UP);
        return Optional.of(result);
    }
}