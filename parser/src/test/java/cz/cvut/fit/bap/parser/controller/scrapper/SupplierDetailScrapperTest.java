package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.SupplierDetailPageData;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SupplierDetailScrapperTest {
    AddressData addressData = new AddressData("Česká republika", "Praha", "10000",
            "Vinohradská", "230", "1511", null);
    String organisationId = "08028656";
    String VATIdNumber = "CZ08028656";
    Boolean isAssociationOfSuppliers = false;

    private final SupplierDetailPageData expectedData = new SupplierDetailPageData(addressData, organisationId,
            VATIdNumber, isAssociationOfSuppliers);

    @Test
    void getPageData() throws IOException {
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-23-V00002372/vysledek/detail-uverejneni/1590322705";

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "SupplierDetail.html"));
        SupplierDetailScrapper supplierDetailScrapper = new SupplierDetailScrapper(document);

        SupplierDetailPageData actualData = supplierDetailScrapper.getPageData();
        Assertions.assertEquals(expectedData, actualData);
    }
}