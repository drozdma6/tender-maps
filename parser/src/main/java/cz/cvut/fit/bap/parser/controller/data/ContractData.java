package cz.cvut.fit.bap.parser.controller.data;

import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
    Row from supplier's table on procurement result page.
 */
public record ContractData(
        BigDecimal price,
        BigDecimal priceVAT,
        BigDecimal priceWithAmend,
        BigDecimal priceWithAmendVAT,
        String detailHref,
        String companyName,
        Currency currency,
        LocalDate contractDate
) {
}
