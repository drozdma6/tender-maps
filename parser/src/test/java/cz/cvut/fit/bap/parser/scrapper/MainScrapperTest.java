package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@SpringBootTest
class MainScrapperTest{
    @Autowired
    private MainScrapper mainScrapper;

    @MockBean
    private ContractorDetailScrapper contractorDetailScrapper;

    @MockBean
    private ContractorCompletedScrapper contractorCompletedScrapper;

    final Address testAddress = new Address("sk", "Bratislava", "16000", "Bratislavska", "65");

    String[] expectedProfiles = {"MVCR", "MPSV", "mmr"};
    ContractorAuthority[] expectedContractorAuthorities = {new ContractorAuthority(
            "Ministerstvo vnitra", expectedProfiles[0], testAddress), new ContractorAuthority(
            "Ministerstvo práce a sociálních věcí", expectedProfiles[1],
            testAddress), new ContractorAuthority("Ministerstvo pro místní rozvoj",
                                                  expectedProfiles[2], testAddress)};

    @BeforeEach
    void setUp() throws IOException{
        when(contractorDetailScrapper.scrape(expectedProfiles[0])).thenAnswer(
                (i -> expectedContractorAuthorities[0]));
        when(contractorDetailScrapper.scrape(expectedProfiles[1])).thenAnswer(
                (i -> expectedContractorAuthorities[1]));
        when(contractorDetailScrapper.scrape(expectedProfiles[2])).thenAnswer(
                (i -> expectedContractorAuthorities[2]));
    }

    @Test
    void scrape() throws IOException{
        mainScrapper.scrape();
        InOrder inOrder = Mockito.inOrder(contractorDetailScrapper, contractorCompletedScrapper);
        inOrder.verify(contractorDetailScrapper, times(1)).scrape(expectedProfiles[0]);
        inOrder.verify(contractorCompletedScrapper, times(1))
                .scrape(expectedContractorAuthorities[0]);

        inOrder.verify(contractorDetailScrapper, times(1)).scrape(expectedProfiles[1]);
        inOrder.verify(contractorCompletedScrapper, times(1))
                .scrape(expectedContractorAuthorities[1]);

        inOrder.verify(contractorDetailScrapper, times(1)).scrape(expectedProfiles[2]);
        inOrder.verify(contractorCompletedScrapper, times(1))
                .scrape(expectedContractorAuthorities[2]);
    }
}