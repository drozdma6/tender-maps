package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;


class ProcurementDetailScrapperTest{
    private final HtmlFileCreator htmlFileCreator = new HtmlFileCreator();

    @Test
    void getContractorAuthorityDto() throws IOException{
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-23-V00002372";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementDetail.html"));
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);

        String expectedUrl = "/en/verejne-zakazky/detail-zakazky/N006-23-V00002372/detail-subjektu/2530226";
        ContractorAuthorityDto expectedDto = new ContractorAuthorityDto(expectedUrl, "Česká agentura na podporu obchodu");
        ContractorAuthorityDto actualDto = procurementDetailScrapper.getContractorAuthorityDto();

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void getProcurementName() throws IOException{
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/p:vz:sort-stavZP=none/detail-zakazky/N006-23-V00002372";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementDetail.html"));
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);

        String expectedProcurementName = "Prodloužení podpory pro Kentico Basic (01/2023)";
        Assertions.assertEquals(expectedProcurementName,
                procurementDetailScrapper.getProcurementName());
    }

    @Test
    void getProcurementNameMissing(){
        String htmlWithoutName = """
                <div title="Code from the CPV code list" class="gov-grid-tile">
                    <h3 class="gov-title--delta">Code from the CPV code list</h3>
                    <p class="gov-note" title="72267100-0">72267100-0</p>
                </div>""";

        Document document = Jsoup.parse(htmlWithoutName);
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);
        Assertions.assertThrows(MissingHtmlElementException.class, procurementDetailScrapper::getProcurementName);
    }

    @Test
    void getProcurementPlaceOfPerformance() throws IOException{
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/p:vz:sort-stavZP=none/detail-zakazky/N006-23-V00002372";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementDetail.html"));
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);

        String expectedPlaceOfPerformance = "Hlavní město Praha";
        Assertions.assertEquals(expectedPlaceOfPerformance,
                procurementDetailScrapper.getProcurementPlaceOfPerformance());
    }

    @Test
    void getProcurementPLaceOfPerformanceNull(){
        String htmlWithoutPlaceOfPerformance = """
                <div title="Code from the CPV code list" class="gov-grid-tile">
                    <h3 class="gov-title--delta">Code from the CPV code list</h3>
                    <p class="gov-note" title="72267100-0">72267100-0</p>
                </div>""";

        Document document = Jsoup.parse(htmlWithoutPlaceOfPerformance);
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);
        Assertions.assertNull(procurementDetailScrapper.getProcurementPlaceOfPerformance());
    }

    @Test
    void getProcurementDateOfPublication() throws IOException{
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/p:vz:sort-stavZP=none/detail-zakazky/N006-23-V00002372";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementDetail.html"));
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);
        LocalDate expectedDateOfPublication = LocalDate.of(2023, 1, 30);
        Assertions.assertEquals(expectedDateOfPublication,
                procurementDetailScrapper.getProcurementDateOfPublication());
    }

    @Test
    void getProcurementDateOfPublicationNull(){
        String htmlWrongDateFormat = """
                <td class="gov-table__cell gov-table__cell--second"
                title="03. 03. 2023 16:48" style="width:150px"
                data-title="Date of publication">03-03-2023 16:48
                </td>""";

        Document document = Jsoup.parse(htmlWrongDateFormat);
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);
        Assertions.assertNull(procurementDetailScrapper.getProcurementDateOfPublication());
    }
}