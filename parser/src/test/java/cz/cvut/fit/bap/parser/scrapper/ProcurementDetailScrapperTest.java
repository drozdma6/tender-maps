package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Procurement;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;


@SpringBootTest
class ProcurementDetailScrapperTest{
    @Autowired
    private ProcurementDetailScrapper procurementDetailScrapper;

    @MockBean
    private IFetcher fetcher;

    @MockBean
    private ProcurementService procurementService;

    @BeforeEach
    void setUp() throws IOException{
        final String url = "https://nen.nipez.cz/en/verejne-zakazky/p:vz:sort-stavZP=none/detail-zakazky/N006-23-V00002372";
        HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementDetail.html"));
        when(fetcher.getProcurementDetail(anyString())).thenReturn(document);
    }

    @Test
    void scrape() throws IOException{
        String expectedProcurementName = "Prodloužení podpory pro Kentico Basic (01/2023)";
        String expectedPlaceOfPerformance = "Hlavní město Praha";
        LocalDate expectedDateOfPublication = LocalDate.of(2023, 1, 30);

        String systemNumber = "test system number";
        Company supplier = new Company();
        ContractorAuthority contractorAuthority = new ContractorAuthority();
        BigDecimal contractPrice = new BigDecimal(10);

        when(procurementService.create(any(Procurement.class))).thenAnswer(i -> i.getArgument(0));
        Procurement expectedProcurement = new Procurement(expectedProcurementName, supplier,
                                                          contractorAuthority, contractPrice,
                                                          expectedPlaceOfPerformance,
                                                          expectedDateOfPublication, systemNumber);
        Procurement actualProcurement = procurementDetailScrapper.scrape(supplier, contractPrice,
                                                                         contractorAuthority,
                                                                         systemNumber);
        verify(fetcher, times(1)).getProcurementDetail(systemNumber);

        Assertions.assertEquals(expectedProcurement.getName(), actualProcurement.getName());
        Assertions.assertEquals(expectedProcurement.getPlaceOfPerformance(),
                                actualProcurement.getPlaceOfPerformance());
        Assertions.assertEquals(expectedProcurement.getDateOfPublication(),
                                actualProcurement.getDateOfPublication());
    }
}