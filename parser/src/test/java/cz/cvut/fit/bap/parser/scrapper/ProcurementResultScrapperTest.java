package cz.cvut.fit.bap.parser.scrapper;


import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.Procurement;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;


@SpringBootTest
class ProcurementResultScrapperTest{
    @Autowired
    private ProcurementResultScrapper procurementResultScrapper;

    @MockBean
    private OfferService offerService;

    @MockBean
    private CompanyDetailScrapper companyDetailScrapper;

    @MockBean
    private ProcurementDetailScrapper procurementDetailScrapper;

    @MockBean
    private AbstractFetcher fetcher;

    private final String systemNumber = "testSystemNumber";
    private final HtmlFileCreator htmlFileCreator = new HtmlFileCreator();


    @BeforeEach
    public void setUp() throws IOException{
        Procurement testProcurement = new Procurement();
        Company testCompany = new Company();
        testProcurement.setId(1L);
        testCompany.setId(1L);
        when(companyDetailScrapper.scrape(anyString(), anyString())).thenReturn(testCompany);
        when(procurementDetailScrapper.scrape(any(), any(), any(), any())).thenReturn(
                testProcurement);
    }

    @Test
    void participantsTest() throws IOException{
        String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult.html"));
        when(fetcher.getProcurementResult(systemNumber)).thenReturn(document);
        BigDecimal[] expectedParticipantsBids = {new BigDecimal("266400.00"), new BigDecimal(
                "378048.00")};
        String[] participantCompaniesDetailHrefs = {"/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846394", "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846393"};
        String[] participantCompaniesNames = {"Nej.cz s.r.o.", "O2 Czech Republic a.s."};
        ArgumentCaptor<Offer> argument = ArgumentCaptor.forClass(Offer.class);

        procurementResultScrapper.scrape(new ContractorAuthority(), systemNumber);

        verify(fetcher, times(1)).getProcurementResult(systemNumber);
        InOrder inOrder = Mockito.inOrder(companyDetailScrapper, offerService);
        inOrder.verify(companyDetailScrapper, times(1))
                .scrape(participantCompaniesDetailHrefs[0], participantCompaniesNames[0]);
        inOrder.verify(offerService, times(1)).create(argument.capture());
        Assertions.assertEquals(expectedParticipantsBids[0], argument.getValue().getPrice());

        inOrder.verify(companyDetailScrapper, times(1))
                .scrape(participantCompaniesDetailHrefs[1], participantCompaniesNames[1]);
        inOrder.verify(offerService, times(1)).create(argument.capture());
        Assertions.assertEquals(expectedParticipantsBids[1], argument.getValue().getPrice());
    }

    @Test
    void testSingleSupplier() throws IOException{
        String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult.html"));
        when(fetcher.getProcurementResult(systemNumber)).thenReturn(document);
        BigDecimal expectedContractPrice = new BigDecimal("266400.00");
        String expectedSupplierDetailHref = "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846408";
        String expectedCompanyName = "Nej.cz s.r.o.";

        procurementResultScrapper.scrape(new ContractorAuthority(), systemNumber);

        verify(fetcher, times(1)).getProcurementResult(systemNumber);
        verify(companyDetailScrapper, times(1)).scrape(expectedSupplierDetailHref,
                                                       expectedCompanyName);
        verify(procurementDetailScrapper, times(1)).scrape(any(), eq(expectedContractPrice), any(),
                                                           any());
    }

    @Test
    void testSingleSupplierMultipleContracts() throws IOException{
        String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-22-V00034336/vysledek/p:vys:page=1-2";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult2.html"));
        when(fetcher.getProcurementResult(systemNumber)).thenReturn(document);
        BigDecimal expectedContractPrice = new BigDecimal("2404874.60");
        String expectedSupplierDetailHref = "/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-22-V00034336/vysledek/p:vys:page=1-2/detail-uverejneni/1612909749";
        String expectedCompanyName = "ACTIVA spol. s r.o.";

        procurementResultScrapper.scrape(new ContractorAuthority(), systemNumber);

        verify(fetcher, times(1)).getProcurementResult(systemNumber);
        verify(companyDetailScrapper, times(1)).scrape(expectedSupplierDetailHref,
                                                       expectedCompanyName);
        verify(procurementDetailScrapper, times(1)).scrape(any(), eq(expectedContractPrice), any(),
                                                           any());
    }

    @Test
    void testMultipleSupplier() throws IOException{
        String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-21-V00030027/vysledek";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult3.html"));
        when(fetcher.getProcurementResult(systemNumber)).thenReturn(document);
        BigDecimal[] expectedContractPrice = {new BigDecimal("13049691.74"), new BigDecimal(
                "17410000.00")};
        String[] expectedSupplierDetailHref = {"/en/verejne-zakazky/detail-zakazky/N006-21-V00030027/vysledek/detail-uverejneni/1606532320", "/en/verejne-zakazky/detail-zakazky/N006-21-V00030027/vysledek/detail-uverejneni/1606532319"};
        String[] expectedCompanyName = {"L&R VASKO Service s. r. o.", "SKYCLEAN s.r.o."};

        procurementResultScrapper.scrape(new ContractorAuthority(), systemNumber);

        //first supplier
        verify(fetcher, times(1)).getProcurementResult(systemNumber);
        verify(companyDetailScrapper, times(1)).scrape(expectedSupplierDetailHref[0],
                                                       expectedCompanyName[0]);
        verify(procurementDetailScrapper, times(1)).scrape(any(), eq(expectedContractPrice[0]),
                                                           any(), any());

        //second supplier
        verify(fetcher, times(1)).getProcurementResult(systemNumber);
        verify(companyDetailScrapper, times(1)).scrape(expectedSupplierDetailHref[1],
                                                       expectedCompanyName[1]);
        verify(procurementDetailScrapper, times(1)).scrape(any(), eq(expectedContractPrice[1]),
                                                           any(), any());
    }

    @Test
    void testMissingInformation() throws IOException{
        String url = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/N006-22-V00005182/vysledek";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult4.html"));
        when(fetcher.getProcurementResult(systemNumber)).thenReturn(document);

        Assertions.assertThrows(MissingHtmlElementException.class,
                                () -> procurementResultScrapper.scrape(new ContractorAuthority(),
                                                                       systemNumber));
    }
}