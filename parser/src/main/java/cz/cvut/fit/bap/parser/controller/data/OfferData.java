package cz.cvut.fit.bap.parser.controller.data;

import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;

import java.math.BigDecimal;

/*
    Row from participants table on procurement result page
 */
public record OfferData(
        BigDecimal price,
        String detailHref,
        String companyName,
        Currency currency
){
}