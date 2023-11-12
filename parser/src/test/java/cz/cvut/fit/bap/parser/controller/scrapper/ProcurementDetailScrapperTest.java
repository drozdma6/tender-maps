package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.ContactPersonData;
import cz.cvut.fit.bap.parser.controller.data.ProcurementDetailPageData;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;


class ProcurementDetailScrapperTest{
    private final String procurementName = "Prodloužení podpory pro Kentico Basic (01/2023)";
    private final String placeOfPerformance = "Hlavní město Praha";
    private final LocalDate dateOfPublication = LocalDate.of(2023, 1, 30);
    private final String contractingAuthorityName = "Česká agentura na podporu obchodu";
    private final String contractingAuthorityUrl = "/en/verejne-zakazky/detail-zakazky/N006-23-V00002372/detail-subjektu/2530226";
    private final String publicContractRegime = "Small-scale public contract";
    private final String type = "Public contract for services";
    private final String typeOfProcedure = "Otevřená výzva";
    private final LocalDate bidsSubmissionDeadline = LocalDate.of(2023, 2, 13);
    private final String codeFromNipezCodeList = "72267100-0";
    private final String nameFromNipezCodeList = "Údržba programového vybavení pro informační technologie";
    private final ContactPersonData contactPersonData = new ContactPersonData("Jan", "Hančl", "jan.hancl@czechtrade.cz");

    private final ProcurementDetailPageData expectedDetailPageData = new ProcurementDetailPageData(
            procurementName,
            placeOfPerformance,
            dateOfPublication,
            contractingAuthorityName,
            contractingAuthorityUrl,
            type,
            typeOfProcedure,
            publicContractRegime,
            bidsSubmissionDeadline,
            codeFromNipezCodeList,
            nameFromNipezCodeList,
            contactPersonData);

    private final HtmlFileCreator htmlFileCreator = new HtmlFileCreator();

    @Test
    void getPageData() throws IOException {
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-23-V00002372";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementDetail.html"));
        ProcurementDetailScrapper procurementDetailScrapper = new ProcurementDetailScrapper(document);
        ProcurementDetailPageData procurementDetailPageData = procurementDetailScrapper.getPageData();
        Assertions.assertEquals(expectedDetailPageData, procurementDetailPageData);
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
        Assertions.assertThrows(MissingHtmlElementException.class, procurementDetailScrapper::getPageData);
    }
}