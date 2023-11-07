package cz.cvut.fit.bap.parser.controller.data;

/*
    Record representing data from offer detail page
 */
public record OfferDetailPageData(
        AddressData addressData,
        String organisationId,
        String VATIdNumber,
        Boolean isRejectedDueTooLow,
        Boolean isWithdrawn,
        Boolean isAssociationOfSuppliers
) {
}
