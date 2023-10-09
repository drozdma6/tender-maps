package cz.cvut.fit.bap.parser.controller.dto;

import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
    Row from supplier's table on procurement result page.
 */
public record ContractDto(
        BigDecimal contractPrice,
        String detailHref,
        String companyName,
        Currency currency,
        LocalDate contractDate
) {
}
