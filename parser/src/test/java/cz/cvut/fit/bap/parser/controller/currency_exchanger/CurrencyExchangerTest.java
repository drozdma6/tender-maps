package cz.cvut.fit.bap.parser.controller.currency_exchanger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CurrencyExchangerTest {

    @Autowired
    private CurrencyExchanger currencyExchanger;

    @Test
    public void testExchangeEurToCzk() {
        List<BigDecimal> result = currencyExchanger.exchange(
                List.of(BigDecimal.valueOf(10)),
                Currency.EUR,
                Currency.CZK,
                LocalDate.of(2023, Month.SEPTEMBER, 29));
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(244.54), result.get(0));
    }

    @Test
    public void testExchangeMultipleUsdToCzk() {
        List<BigDecimal> result = currencyExchanger.exchange(
                List.of(BigDecimal.TEN, BigDecimal.ONE),
                Currency.USD,
                Currency.CZK,
                LocalDate.of(2023, Month.SEPTEMBER, 29));
        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(230.99), result.get(0));
        assertEquals(BigDecimal.valueOf(2310, 2), result.get(1));
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