package cz.cvut.fit.bap.parser.scrapper;


import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.Procurement;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
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
    private IFetcher fetcher;

    private final String systemNumber = "testSystemNumber";

    @BeforeEach
    public void setUp() throws IOException{
        String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek";
        HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult.html"));

        Procurement testProcurement = new Procurement();
        testProcurement.setId(1L);
        Company testCompany = new Company();
        testCompany.setId(1L);

        when(fetcher.getProcurementResult(systemNumber)).thenReturn(document);
        when(companyDetailScrapper.scrape(anyString())).thenReturn(testCompany);
        when(procurementDetailScrapper.scrape(any(), any(), any(), any())).thenReturn(
                testProcurement);
    }

    @Test
    void participantsTest() throws IOException{
        BigDecimal[] expectedParticipantsBids = {new BigDecimal("266400.00"), new BigDecimal(
                "378048.00")};
        String[] participantCompaniesDetailHrefs = {"/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846394", "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846393"};
        ArgumentCaptor<Offer> argument = ArgumentCaptor.forClass(Offer.class);

        procurementResultScrapper.scrape(new ContractorAuthority(), systemNumber);

        verify(fetcher, times(1)).getProcurementResult(systemNumber);
        InOrder inOrder = Mockito.inOrder(companyDetailScrapper, offerService);

        inOrder.verify(companyDetailScrapper, times(1)).scrape(participantCompaniesDetailHrefs[0]);
        inOrder.verify(offerService, times(1)).create(argument.capture());
        Assertions.assertEquals(expectedParticipantsBids[0], argument.getValue().getPrice());

        inOrder.verify(companyDetailScrapper, times(1)).scrape(participantCompaniesDetailHrefs[1]);
        inOrder.verify(offerService, times(1)).create(argument.capture());
        Assertions.assertEquals(expectedParticipantsBids[1], argument.getValue().getPrice());
    }

    @Test
    void testSupplier() throws IOException{
        BigDecimal expectedContractPrice = new BigDecimal("266400.00");
        String expectedSupplierDetailHref = "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846408";
        ArgumentCaptor<BigDecimal> argumentCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        procurementResultScrapper.scrape(null, systemNumber);

        verify(fetcher, times(1)).getProcurementResult(systemNumber);
        verify(companyDetailScrapper, times(1)).scrape(expectedSupplierDetailHref);
        verify(procurementDetailScrapper, times(1)).scrape(any(), argumentCaptor.capture(), any(),
                                                           any());
        Assertions.assertEquals(expectedContractPrice, argumentCaptor.getValue());
    }
}