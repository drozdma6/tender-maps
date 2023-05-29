package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class ContractorCompletedScrapperTest{
    @Test
    void getProcurementSystemNumbers() throws IOException{
        final String urlFirstPage = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/p:puvz:zadavatelNazev=Ministerstvo%20financ%C3%AD&datumPrvniUver=2023-02-01,2023-03-27&stavZP=zadana&page=1";
        final String[] expectedSystemNumbers = {"N006/23/V00004206", "N006/23/V00005110", "N006/23/V00004922"};

        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(urlFirstPage, "ContractorCompleted.html"));
        ContractorCompletedScrapper contractorCompletedScrapper = new ContractorCompletedScrapper(document);

        List<String> actualSystemNumbers = contractorCompletedScrapper.getProcurementSystemNumbers("Ministerstvo financí");

        Assertions.assertEquals(expectedSystemNumbers.length, actualSystemNumbers.size());

        for(int i = 0; i < expectedSystemNumbers.length; i++){
            Assertions.assertEquals(expectedSystemNumbers[i], actualSystemNumbers.get(i));
        }
    }

    @Test
    public void testGetProcurementSystemNumbers_WithNoMatchingAuthorityName(){
        String html = "<table>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"Contracting authority\">Contracting Authority 1</td>" +
                "<td data-title=\"NEN system number\">123</td>" +
                "</tr>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"Contracting authority\">Contracting Authority 2</td>" +
                "<td data-title=\"NEN system number\">456</td>" +
                "</tr>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"Contracting authority\">Contracting Authority 1</td>" +
                "<td data-title=\"NEN system number\">789</td>" +
                "</tr>" +
                "</table>";
        Document document = Jsoup.parse(html);
        ContractorCompletedScrapper scrapper = new ContractorCompletedScrapper(document);

        List<String> systemNumbers = scrapper.getProcurementSystemNumbers("Non-existing Authority");

        Assertions.assertEquals(0, systemNumbers.size());
    }

    @Test
    public void testGetProcurementSystemNumbers_NullAuthorityElem(){
        String html = "<table>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"NEN system number\">123</td>" +
                "</tr>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"Contracting authority\">Contracting Authority 2</td>" +
                "<td data-title=\"NEN system number\">456</td>" +
                "</tr>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"NEN system number\">789</td>" +
                "</tr>" +
                "</table>";
        Document document = Jsoup.parse(html);
        ContractorCompletedScrapper scrapper = new ContractorCompletedScrapper(document);

        List<String> systemNumbers = scrapper.getProcurementSystemNumbers("Contracting Authority 1");

        Assertions.assertEquals(0, systemNumbers.size());
    }

    @Test
    public void testGetProcurementSystemNumbers_NullSystemNumber(){
        String html = "<table>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"Contracting authority\">Contracting Authority 1</td>" +
                "</tr>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"Contracting authority\">Contracting Authority 2</td>" +
                "</tr>" +
                "<tr class=\"gov-table__row\">" +
                "<td data-title=\"Contracting authority\">Contracting Authority 1</td>" +
                "</tr>" +
                "</table>";
        Document document = Jsoup.parse(html);
        ContractorCompletedScrapper scrapper = new ContractorCompletedScrapper(document);

        List<String> systemNumbers = scrapper.getProcurementSystemNumbers("Contracting Authority 1");

        Assertions.assertEquals(0, systemNumbers.size());
    }
}