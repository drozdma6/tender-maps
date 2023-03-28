package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
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
    private AbstractFetcher fetcher;


    private final HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
    private final Address contractorAddress = new Address("CZ", "Praha", "11800", "Letenská", "15");
    private final ContractorAuthority contractorAuthority = new ContractorAuthority(
            "Ministerstvo financí", "mfcr", contractorAddress);

    private final String[] expectedSystemNumbers = {"N006/23/V00004206", "N006/23/V00005110", "N006/23/V00004922"};


    @BeforeEach
    void setUp() throws IOException{
        final String urlFirstPage = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/p:puvz:zadavatelNazev=Ministerstvo%20financ%C3%AD&datumPrvniUver=2023-02-01,2023-03-27&stavZP=zadana&page=1";
        final String urlSecondPage = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/p:puvz:zadavatelNazev=Ministerstvo%20financ%C3%AD&datumPrvniUver=2023-02-01,2023-03-27&stavZP=zadana&page=2";

        Document documentFirstPage = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(urlFirstPage, "ContractorCompleted.html"));
        Document documentSecondPage = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(urlSecondPage, "ContractorCompleted2.html"));


        when(fetcher.getContractorCompleted(contractorAuthority.getProfile(),
                                            contractorAuthority.getName(), 1)).thenReturn(
                documentFirstPage);

        when(fetcher.getContractorCompleted(contractorAuthority.getProfile(),
                                            contractorAuthority.getName(), 2)).thenReturn(
                documentSecondPage);

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
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void endScrappingAlreadySavedProc() throws IOException{
        when(procurementService.existsBySystemNumber(expectedSystemNumbers[0])).thenReturn(false);
        when(procurementService.existsBySystemNumber(expectedSystemNumbers[1])).thenReturn(true);

        contractorCompletedScrapper.scrape(contractorAuthority);

        InOrder inOrder = Mockito.inOrder(procurementResultScrapper);
        inOrder.verify(procurementResultScrapper, times(1))
                .scrape(contractorAuthority, expectedSystemNumbers[0]);
        inOrder.verify(procurementResultScrapper, never())
                .scrape(contractorAuthority, expectedSystemNumbers[1]);
        inOrder.verify(procurementResultScrapper, never())
                .scrape(contractorAuthority, expectedSystemNumbers[2]);
        inOrder.verifyNoMoreInteractions();
    }
}