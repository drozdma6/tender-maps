package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ContractorDetailScrapperTest{
    @Test
    void geContractorAuthorityAddress() throws IOException{
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-22-V00012195/detail-subjektu/409845860";

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "ContractorDetail.html"));
        ContractorDetailScrapper contractorDetailScrapper = new ContractorDetailScrapper(document);

        AddressDto expectedAddress = new AddressDto("CZ", "Orlová", "73514", "Osvobození", null);
        AddressDto actualAddress = contractorDetailScrapper.getContractorAuthorityAddress();
        Assertions.assertEquals(actualAddress, expectedAddress);
    }

    @Test
    void getContractorAuthorityUrl() throws IOException{
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-22-V00012195/detail-subjektu/409845860";

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "ContractorDetail.html"));
        ContractorDetailScrapper contractorDetailScrapper = new ContractorDetailScrapper(document);

        String actualName = contractorDetailScrapper.getContractorAuthorityUrl();
        Assertions.assertEquals("https://nen.nipez.cz/profil/MESTOORLOVA", actualName);
    }
}