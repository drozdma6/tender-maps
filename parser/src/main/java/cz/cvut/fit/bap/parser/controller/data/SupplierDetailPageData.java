package cz.cvut.fit.bap.parser.controller.data;

import java.math.BigDecimal;

/*
    Record representing data from supplier detail page
 */
public record SupplierDetailPageData(
        AddressData addressData,
        BigDecimal contractPriceVAT,
        BigDecimal contractPriceWithAmendments,
        BigDecimal contractPriceWithAmendmentsVAT,
        String organisationId,
        String VATIdNumber,
        Boolean isAssociationOfSuppliers
) {
}
