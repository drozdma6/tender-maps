package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.mockito.Mockito.*;


@SpringBootTest
class ContractorCompletedScrapperTest{
    @Autowired
    private ContractorCompletedScrapper contractorCompletedScrapper;

    @MockBean
    private ProcurementResultScrapper procurementResultScrapper;

    @MockBean
    private ProcurementService procurementService;

    @MockBean
    private IFetcher fetcher;

    private final Address contractorAddress = new Address("CZ", "Praha", "17000", "Nad štolou",
                                                          "3");
    private final ContractorAuthority contractorAuthority = new ContractorAuthority(
            "Ministerstvo vnitra", "MVCR", contractorAddress);

    private final String[] expectedSystemNumbers = {"N006/20/V00022104", "N006/22/V00017426", "N006/22/V00031753", "N006/23/V00006632"};

    @BeforeEach
    void setUp() throws IOException{
        final String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR/uzavrene-zakazky";
        HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ContractorCompleted.html"));
        when(fetcher.getContractorCompleted(contractorAuthority.getProfile(),
                                            contractorAuthority.getName())).thenReturn(document);
    }

    @Test
    void scrape() throws IOException{
        when(procurementService.existsBySystemNumber(anyString())).thenReturn(false);

        contractorCompletedScrapper.scrape(contractorAuthority);

        InOrder inOrder = Mockito.inOrder(procurementResultScrapper);
        inOrder.verify(procurementResultScrapper, times(1))
                .scrape(contractorAuthority, expectedSystemNumbers[0]);
        inOrder.verify(procurementResultScrapper, times(1))
                .scrape(contractorAuthority, expectedSystemNumbers[1]);
        inOrder.verify(procurementResultScrapper, times(1))
                .scrape(contractorAuthority, expectedSystemNumbers[2]);
        inOrder.verify(procurementResultScrapper, times(1))
                .scrape(contractorAuthority, expectedSystemNumbers[3]);
    }

    @Test
    void endScrapping() throws IOException{
        when(procurementService.existsBySystemNumber(expectedSystemNumbers[2])).thenReturn(true);

        contractorCompletedScrapper.scrape(contractorAuthority);

        InOrder inOrder = Mockito.inOrder(procurementResultScrapper);
        inOrder.verify(procurementResultScrapper, times(1))
                .scrape(contractorAuthority, expectedSystemNumbers[0]);
        inOrder.verify(procurementResultScrapper, times(1))
                .scrape(contractorAuthority, expectedSystemNumbers[1]);
        inOrder.verify(procurementResultScrapper, never())
                .scrape(contractorAuthority, expectedSystemNumbers[2]);
        inOrder.verify(procurementResultScrapper, never())
                .scrape(contractorAuthority, expectedSystemNumbers[3]);
    }
}