package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;


class ContractorListScrapperTest{
    @Test
    void getAuthoritiesHrefs() throws IOException{
        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile("url", "ContractorList.html"));
        ContractorListScrapper contractorListScrapper = new ContractorListScrapper(document);

        List<ContractorAuthorityDto> actualContractorAuthorityList = contractorListScrapper.getAuthoritiesHrefs();

        Assertions.assertEquals(50, actualContractorAuthorityList.size());
        Assertions.assertEquals(actualContractorAuthorityList.get(0), new ContractorAuthorityDto("/en/profily-zadavatelu-platne/detail-profilu/1415BI", "Zlínský kraj"));
        Assertions.assertEquals(actualContractorAuthorityList.get(1), new ContractorAuthorityDto("/en/profily-zadavatelu-platne/detail-profilu/7usro", "7U s.r.o."));
    }
}