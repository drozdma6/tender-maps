package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ContractorDetailScrapperTest{
    @Autowired
    private ContractorDetailScrapper contractorDetailScrapper;

    @MockBean
    private AbstractFetcher fetcher;

    @MockBean
    private ContractorAuthorityService contractorAuthorityService;

    @MockBean
    private AddressService addressService;

    private Address expectedAddress;
    private ContractorAuthority expectedContractorAuthority;
    private final String profile = "MVCR";
    private final String officialName = "Ministerstvo vnitra";

    @BeforeEach
    void setUp() throws IOException{
        final String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR";
        HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ContractorDetail.html"));
        when(fetcher.getContractorDetail(profile)).thenReturn(document);
        expectedAddress = new Address("CZ", "Praha", "17000", "Nad štolou", "3");
        expectedContractorAuthority = new ContractorAuthority(officialName, profile,
                                                              expectedAddress);
        when(contractorAuthorityService.create(any(ContractorAuthority.class))).thenAnswer(
                i -> i.getArgument(0));
        when(addressService.create(any(Address.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void scrapeNonExistingContractor() throws IOException{
        when(contractorAuthorityService.readByName(officialName)).thenReturn(Optional.empty());
        ContractorAuthority contractorAuthority = contractorDetailScrapper.scrape(profile);
        Assertions.assertEquals(expectedContractorAuthority.getName(),
                                contractorAuthority.getName());
        Assertions.assertEquals(expectedContractorAuthority.getProfile(),
                                contractorAuthority.getProfile());

        Assertions.assertEquals(expectedAddress.getCountryCode(),
                                contractorAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAddress.getCity(),
                                contractorAuthority.getAddress().getCity());
        Assertions.assertEquals(expectedAddress.getPostalCode(),
                                contractorAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAddress.getStreet(),
                                contractorAuthority.getAddress().getStreet());
        Assertions.assertEquals(expectedAddress.getBuildingNumber(),
                                contractorAuthority.getAddress().getBuildingNumber());
    }

    @Test
    void scrapeExistingContractor() throws IOException{
        when(contractorAuthorityService.readByName(officialName)).thenReturn(
                Optional.of(expectedContractorAuthority));
        ContractorAuthority contractorAuthority = contractorDetailScrapper.scrape(profile);
        Assertions.assertEquals(expectedContractorAuthority.getName(),
                                contractorAuthority.getName());
        Assertions.assertEquals(expectedContractorAuthority.getProfile(),
                                contractorAuthority.getProfile());

        Assertions.assertEquals(expectedAddress.getCountryCode(),
                                contractorAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAddress.getCity(),
                                contractorAuthority.getAddress().getCity());
        Assertions.assertEquals(expectedAddress.getPostalCode(),
                                contractorAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAddress.getStreet(),
                                contractorAuthority.getAddress().getStreet());
        Assertions.assertEquals(expectedAddress.getBuildingNumber(),
                                contractorAuthority.getAddress().getBuildingNumber());
    }
}