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
        final String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR";

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "ContractorDetail.html"));
        ContractorDetailScrapper contractorDetailScrapper = new ContractorDetailScrapper(document);

        AddressDto expectedAddress = new AddressDto("CZ", "Praha", "17000", "Nad štolou", "3");
        AddressDto actualAddress = contractorDetailScrapper.getContractorAuthorityAddress();
        Assertions.assertEquals(actualAddress, expectedAddress);
    }

    @Test
    void getContractorAuthorityName() throws IOException{
        final String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR";

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "ContractorDetail.html"));
        ContractorDetailScrapper contractorDetailScrapper = new ContractorDetailScrapper(document);
        String actualName = contractorDetailScrapper.getContractorAuthorityName();
        Assertions.assertEquals("Ministerstvo vnitra", actualName);
    }
}