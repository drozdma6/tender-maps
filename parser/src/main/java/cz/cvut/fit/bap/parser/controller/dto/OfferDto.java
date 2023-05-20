package cz.cvut.fit.bap.parser.controller.dto;

import java.math.BigDecimal;

/*
    Class represents offer information scrapped from procurement result page table row
 */
public record OfferDto(
        BigDecimal contractPrice,
        String detailHref,
        String companyName
){
    public OfferDto addPriceToOffer(BigDecimal newOfferPrice){
        BigDecimal newPrice = contractPrice;
        if(newOfferPrice != null){
            newPrice = (newPrice != null) ? newPrice.add(newOfferPrice) : newOfferPrice;
        }
        return new OfferDto(newPrice, detailHref, companyName);
    }
}