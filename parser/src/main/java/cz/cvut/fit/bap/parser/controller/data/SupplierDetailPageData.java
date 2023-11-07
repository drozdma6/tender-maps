package cz.cvut.fit.bap.parser.controller.data;


/*
    Record representing data from supplier detail page
 */
public record SupplierDetailPageData(
        AddressData addressData,
        String organisationId,
        String VATIdNumber,
        Boolean isAssociationOfSuppliers
) {
}
