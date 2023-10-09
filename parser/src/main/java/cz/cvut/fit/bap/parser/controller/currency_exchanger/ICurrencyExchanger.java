package cz.cvut.fit.bap.parser.controller.currency_exchanger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ICurrencyExchanger {

    /**
     * Gets exchanged value.
     *
     * @param value value in currencyFrom
     * @param from  currency form
     * @param to    currency to
     * @param date  when to get exchanged data from
     * @return exchanged value from currency from to currency to.
     */
    Optional<BigDecimal> exchange(BigDecimal value, Currency from, Currency to, LocalDate date);
}
