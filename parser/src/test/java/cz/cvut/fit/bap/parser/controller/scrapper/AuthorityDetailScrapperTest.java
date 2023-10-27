package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class AuthorityDetailScrapperTest {
    @Test
    void geContractingAuthorityAddress() throws IOException {
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-22-V00012195/detail-subjektu/409845860";

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "AuthorityDetail.html"));
        AuthorityDetailScrapper authorityDetailScrapper = new AuthorityDetailScrapper(document);

        AddressData expectedAddress = new AddressData("CZ", "Orlová", "73514", "Osvobození", null);
        AddressData actualAddress = authorityDetailScrapper.getContractingAuthorityAddress();
        Assertions.assertEquals(actualAddress, expectedAddress);
    }

    @Test
    void getContractingAuthorityUrl() throws IOException {
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-22-V00012195/detail-subjektu/409845860";

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "AuthorityDetail.html"));
        AuthorityDetailScrapper authorityDetailScrapper = new AuthorityDetailScrapper(document);

        String actualName = authorityDetailScrapper.getContractingAuthorityUrl();
        Assertions.assertEquals("https://nen.nipez.cz/profil/MESTOORLOVA", actualName);
    }
}