package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.OfferDetailPageData;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class OfferDetailScrapperTest {
    private final AddressData addressData = new AddressData("Česká republika", "Hlučín", "74801",
            null, null, null, null);
    private final String organisationId = "64087379";
    private final String VATIdNumber = "CZ64087379";
    private final Boolean isRejectedDueTooLow = false;
    private final Boolean isWithdrawn = false;
    private final Boolean isAssociationOfSuppliers = false;
    private final OfferDetailPageData expectedData = new OfferDetailPageData(addressData, organisationId, VATIdNumber,
            isRejectedDueTooLow, isWithdrawn, isAssociationOfSuppliers);

    @Test
    void getPageData() throws IOException {
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-22-V00012195/vysledek/detail-uverejneni/1444378963";
        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "OfferDetail.html"));
        OfferDetailScrapper offerDetailScrapper = new OfferDetailScrapper(document);
        OfferDetailPageData actualData = offerDetailScrapper.getPageData();
        Assertions.assertEquals(expectedData, actualData);
    }
}