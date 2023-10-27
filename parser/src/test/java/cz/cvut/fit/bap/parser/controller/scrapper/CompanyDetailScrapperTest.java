package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CompanyDetailScrapperTest{
    @Test
    void getCompanyAddress() throws IOException{
        HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
        String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-23-V00002372/vysledek/detail-uverejneni/1590322705";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "CompanyDetail.html"));
        CompanyDetailScrapper companyDetailScrapper = new CompanyDetailScrapper(document);

        AddressData expectedAddress = new AddressData("CZ", "Praha", "10000", "Vinohradská", "230");

        AddressData actualAddress = companyDetailScrapper.getCompanyAddress();

        Assertions.assertEquals(expectedAddress.getCountryCode(),
                actualAddress.getCountryCode());
        Assertions.assertEquals(expectedAddress.city(), actualAddress.city());
        Assertions.assertEquals(expectedAddress.postalCode(),
                actualAddress.postalCode());
        Assertions.assertEquals(expectedAddress.street(), actualAddress.street());
        Assertions.assertEquals(expectedAddress.buildingNumber(),
                actualAddress.buildingNumber());
    }

    @Test
    void getCompanyAddressForeign(){
        String html = """
                <div title="Municipality" class="gov-grid-tile">
                                                <h3 class="gov-title--delta">Municipality</h3>
                                                <p class="gov-note" title="Bratislava">Bratislava</p>
                                            </div>
                                            <div title="Part of municipality" class="gov-grid-tile">
                                                <h3 class="gov-title--delta">Part of municipality</h3>
                                                <p class="gov-note" title="Strašnice">Strašnice</p>
                                            </div>
                                            <div title="Street" class="gov-grid-tile">
                                                <h3 class="gov-title--delta">Street</h3>
                                                <p class="gov-note" title="Vinohradská">Vinohradská</p>
                                            </div>
                                            <div title="Land registry number" class="gov-grid-tile">
                                                <h3 class="gov-title--delta">Land registry number</h3>
                                                <p class="gov-note" title="1511">1511</p>
                                            </div>
                                            <div title="Building number" class="gov-grid-tile">
                                                <h3 class="gov-title--delta">Building number</h3>
                                                <p class="gov-note" title="230">230</p>
                                            </div>
                                            <div title="Postal code" class="gov-grid-tile">
                                                <h3 class="gov-title--delta">Postal code</h3>
                                                <p class="gov-note" title="10000">10000</p>
                                            </div>
                                            <div title="State" class="gov-grid-tile">
                                                <h3 class="gov-title--delta">State</h3>
                                                <p class="gov-note" title="Slovensko">Slovensko</p>
                                            </div>
                """;
        Document document = Jsoup.parse(html);
        CompanyDetailScrapper companyDetailScrapper = new CompanyDetailScrapper(document);

        AddressData expectedAddress = new AddressData("Slovensko", "Bratislava", "10000", "Vinohradská", "230");

        AddressData actualAddress = companyDetailScrapper.getCompanyAddress();
        Assertions.assertEquals(expectedAddress.getCountryCode(),
                actualAddress.getCountryCode());
        Assertions.assertEquals(expectedAddress.getCountryCode(),
                actualAddress.getCountryCode());
        Assertions.assertEquals(expectedAddress.city(), actualAddress.city());
        Assertions.assertEquals(expectedAddress.postalCode(),
                actualAddress.postalCode());
        Assertions.assertEquals(expectedAddress.street(), actualAddress.street());
        Assertions.assertEquals(expectedAddress.buildingNumber(),
                actualAddress.buildingNumber());

    }

    @Test
    void getOrganisationId() throws IOException{
        HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
        String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-23-V00002372/vysledek/detail-uverejneni/1590322705";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "CompanyDetail.html"));
        CompanyDetailScrapper companyDetailScrapper = new CompanyDetailScrapper(document);
        Assertions.assertEquals("08028656", companyDetailScrapper.getOrganisationId());
    }

    @Test
    void getOrganisationIdNull(){
        String html = "<div title=\"Official name\" class=\"gov-grid-tile gov-grid-tile--fill-grid\"><h3 class=\"gov-title--delta\">Official name</h3><p class=\"gov-note\" title=\"CES EA s.r.o.\">CES EA s.r.o.</p></div>";
        Document document = Jsoup.parse(html);
        CompanyDetailScrapper companyDetailScrapper = new CompanyDetailScrapper(document);
        Assertions.assertNull(companyDetailScrapper.getOrganisationId());
    }
}