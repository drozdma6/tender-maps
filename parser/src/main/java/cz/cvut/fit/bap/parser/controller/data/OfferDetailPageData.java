package cz.cvut.fit.bap.parser.controller.data;

import java.math.BigDecimal;

/*
    Record representing data from offer detail page
 */
public record OfferDetailPageData(
        AddressData addressData,
        BigDecimal priceVAT,
        String organisationId,
        String VATIdNumber,
        Boolean isRejectedDueTooLow,
        Boolean isWithdrawn,
        Boolean isAssociationOfSuppliers
) {
}
