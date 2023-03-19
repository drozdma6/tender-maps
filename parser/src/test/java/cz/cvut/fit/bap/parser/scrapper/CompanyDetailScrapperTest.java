package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
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
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
public class CompanyDetailScrapperTest{
    @Autowired
    private CompanyDetailScrapper companyDetailScrapper;

    @MockBean
    private IFetcher fetcher;

    @MockBean
    private AddressService addressService;

    @MockBean
    private CompanyService companyService;

    private Address expectedAddress;
    private Company expectedCompany;
    private final String officialName = "CES EA s.r.o.";
    private final String url = "https://nen.nipez.cz/en/verejne-zakazky/p:vz:sort-stavZP=none/detail-zakazky/N006-23-V00002372/vysledek/detail-uverejneni/1590322705";


    @BeforeEach
    public void setUp() throws IOException{
        HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "CompanyDetail.html"));
        expectedAddress = new Address("Česká republika", "Praha", "10000", "Vinohradská", "230");
        expectedCompany = new Company(officialName, expectedAddress);
        when(fetcher.getCompanyDetail(url)).thenReturn(document);
    }

    @Test
    void testNewCompany() throws IOException{
        when(companyService.readByName(officialName)).thenReturn(Optional.empty());
        when(addressService.create(any(Address.class))).thenAnswer(i -> i.getArgument(0));
        when(companyService.create(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        Company actualCompany = companyDetailScrapper.scrape(url);
        verify(fetcher, times(1)).getCompanyDetail(url);
        verify(companyService, times(1)).readByName(officialName);
        verify(companyService, times(1)).create(expectedCompany);
        verify(addressService, times(1)).create(expectedAddress);
        Assertions.assertEquals(expectedCompany.getName(), actualCompany.getName());
        Assertions.assertEquals(expectedAddress.getCountryCode(),
                                actualCompany.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAddress.getCity(), actualCompany.getAddress().getCity());
        Assertions.assertEquals(expectedAddress.getPostalCode(),
                                actualCompany.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAddress.getStreet(),
                                actualCompany.getAddress().getStreet());
        Assertions.assertEquals(expectedAddress.getBuildingNumber(),
                                actualCompany.getAddress().getBuildingNumber());
    }

    @Test
    void testExistingCompany() throws IOException{
        when(companyService.readByName(officialName)).thenReturn(Optional.of(expectedCompany));
        when(addressService.create(any(Address.class))).thenAnswer(i -> i.getArgument(0));
        when(companyService.create(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        Company actualCompany = companyDetailScrapper.scrape(url);
        verify(fetcher, times(1)).getCompanyDetail(url);
        verify(companyService, times(1)).readByName(officialName);
        verify(companyService, never()).create(expectedCompany);
        verify(addressService, never()).create(expectedAddress);
        Assertions.assertEquals(expectedCompany.getName(), actualCompany.getName());
        Assertions.assertEquals(expectedAddress.getCountryCode(),
                                actualCompany.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAddress.getCity(), actualCompany.getAddress().getCity());
        Assertions.assertEquals(expectedAddress.getPostalCode(),
                                actualCompany.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAddress.getStreet(),
                                actualCompany.getAddress().getStreet());
        Assertions.assertEquals(expectedAddress.getBuildingNumber(),
                                actualCompany.getAddress().getBuildingNumber());
    }
}