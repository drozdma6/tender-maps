package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.AuthorityDetailPageData;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class AuthorityDetailScrapperTest {
    private final String profileUrl = "https://nen.nipez.cz/profil/MESTOORLOVA";
    private final AddressData addressData = new AddressData(null, "Orlová", "73514", "Osvobození", null, "796", "CZ");
    private final AuthorityDetailPageData expectedData = new AuthorityDetailPageData(profileUrl, addressData);

    @Test
    void testGetPageData() throws IOException {
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-22-V00012195/detail-subjektu/409845860";
        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "AuthorityDetail.html"));
        AuthorityDetailScrapper authorityDetailScrapper = new AuthorityDetailScrapper(document);

        AuthorityDetailPageData actualData = authorityDetailScrapper.getPageData();
        Assertions.assertEquals(expectedData, actualData);
    }
}