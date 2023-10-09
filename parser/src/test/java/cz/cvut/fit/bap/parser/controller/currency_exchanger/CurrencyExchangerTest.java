package cz.cvut.fit.bap.parser.controller.currency_exchanger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CurrencyExchangerTest {

    @Autowired
    private CurrencyExchanger currencyExchanger;

    @Test
    public void testExchangeEurToCzk() {
        Optional<BigDecimal> result = currencyExchanger.exchange(
                BigDecimal.valueOf(10),
                Currency.EUR,
                Currency.CZK,
                LocalDate.of(2023, Month.SEPTEMBER, 29));
        Assertions.assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(244.54), result.get());
    }

    @Test
    public void testExchangeUsdToCzk() {
        Optional<BigDecimal> result = currencyExchanger.exchange(
                BigDecimal.valueOf(10),
                Currency.USD,
                Currency.CZK,
                LocalDate.of(2023, Month.SEPTEMBER, 29));
        Assertions.assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(230.99), result.get());
    }

    @Test
    public void invalidApiKey() {
        CurrencyExchanger currencyExchangerWithWrongApiKey = new CurrencyExchanger("wrongApiKey");
        Assertions.assertThrows(RuntimeException.class, () -> currencyExchangerWithWrongApiKey.exchange(
                null,
                Currency.AFN,
                Currency.CZK,
                null));
    }
}