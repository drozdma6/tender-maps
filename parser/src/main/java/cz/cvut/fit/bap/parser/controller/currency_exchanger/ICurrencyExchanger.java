package cz.cvut.fit.bap.parser.controller.currency_exchanger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ICurrencyExchanger {

    /**
     * Gets exchanged values in same order as in provided values list. It takes list to avoid sending multiple request
     * to API for same rates.
     *
     * @param values values to be exchanged in currencyFrom
     * @param from  currency form
     * @param to    currency to
     * @param date  when to get exchanged data from
     * @return exchanged value from currency from to currency to.
     */
    List<BigDecimal> exchange(List<BigDecimal> values, Currency from, Currency to, LocalDate date);
}
