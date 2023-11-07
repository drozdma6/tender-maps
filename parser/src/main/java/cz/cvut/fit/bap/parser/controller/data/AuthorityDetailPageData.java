package cz.cvut.fit.bap.parser.controller.data;

/*
    Record represents data scrapped from contracting authority detail page
 */
public record AuthorityDetailPageData(
        String nenProfileUrl,
        AddressData addressData
) {
}
