package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.CompanyDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.CompanyDetailFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CompanyControllerTest{
    @InjectMocks
    private CompanyController companyController;

    @Mock
    private AddressController addressController;

    @Mock
    private AbstractFetcher fetcher;

    @Mock
    private CompanyService companyService;

    @Mock
    private CompanyDetailFactory companyDetailFactory;

    @Test
    void saveCompanyNewCompany(){
        Address address = new Address("CZ", "Praha", "16000", "Bratislavska", "65");
        Company company = new Company("Test company", address, "organisationId");
        when(companyService.readByName(company.getName())).thenReturn(Optional.empty());
        when(companyService.create(company)).thenAnswer(i -> i.getArgument(0));

        Company savedCompany = companyController.saveCompany(company);
        verify(addressController).saveAddress(any(Address.class));
        verify(companyService).create(any(Company.class));
        Assertions.assertEquals(company.getName(), savedCompany.getName());
        Assertions.assertEquals(company.getOrganisationId(), savedCompany.getOrganisationId());
    }

    @Test
    void saveCompanyExisting(){
        Address address = new Address("CZ", "Praha", "16000", "Bratislavska", "65");
        Company company = new Company("Test company", address, "organisationId");
        when(companyService.readByName(company.getName())).thenReturn(Optional.of(company));

        Company savedCompany = companyController.saveCompany(company);
        verify(addressController, never()).saveAddress(any(Address.class));
        verify(companyService, never()).create(any(Company.class));
        Assertions.assertEquals(company.getName(), savedCompany.getName());
        Assertions.assertEquals(company.getOrganisationId(), savedCompany.getOrganisationId());
    }

    @Test
    void getCompanyExisting(){
        Address address = new Address("CZ", "Praha", "16000", "Bratislavska", "65");
        Company expectedCompany = new Company("Test company", address, "organisationId");
        String testUrl = "testUrl";
        when(companyService.readByName(expectedCompany.getName())).thenReturn(Optional.of(expectedCompany));
        Company actualCompany = companyController.getCompany(testUrl, expectedCompany.getName()).join();

        verify(fetcher, never()).getCompanyDetail(testUrl);
        verify(addressController, never()).geocode(any(AddressDto.class));

        Assertions.assertEquals(expectedCompany.getName(), actualCompany.getName());
        Assertions.assertEquals(expectedCompany.getOrganisationId(), actualCompany.getOrganisationId());
    }

    @Test
    void getCompanyNonExisting(){
        AddressDto addressDto = new AddressDto("CZ", "Praha", "16000", "Bratislavska", "65");
        Address expetedAddress = new Address("CZ", "Praha", "16000", "Bratislavska", "65");
        Company expectedCompany = new Company("nameCompany", expetedAddress, "organisationId");
        String testUrl = "testUrl";
        Document testDoc = new Document(testUrl);
        CompanyDetailScrapper companyDetailScrapper = mock(CompanyDetailScrapper.class);

        when(fetcher.getCompanyDetail(testUrl)).thenReturn(testDoc);
        when(companyDetailFactory.create(testDoc)).thenReturn(companyDetailScrapper);
        when(companyService.readByName(expectedCompany.getName())).thenReturn(Optional.empty());
        when(companyDetailScrapper.getCompanyAddress()).thenReturn(addressDto);
        when(companyDetailScrapper.getOrganisationId()).thenReturn(expectedCompany.getOrganisationId());
        when(addressController.geocode(addressDto)).thenReturn(expetedAddress);

        Company actualCompany = companyController.getCompany(testUrl, expectedCompany.getName()).join();

        verify(fetcher).getCompanyDetail(testUrl);
        verify(addressController).geocode(any(AddressDto.class));

        Assertions.assertEquals(expectedCompany.getName(), actualCompany.getName());
        Assertions.assertEquals(expectedCompany.getOrganisationId(), actualCompany.getOrganisationId());
        Assertions.assertEquals(expectedCompany.getAddress().getCountryCode(), actualCompany.getAddress().getCountryCode());
        Assertions.assertEquals(expectedCompany.getAddress().getCity(), actualCompany.getAddress().getCity());
        Assertions.assertEquals(expectedCompany.getAddress().getPostalCode(), actualCompany.getAddress().getPostalCode());
        Assertions.assertEquals(expectedCompany.getAddress().getStreet(), actualCompany.getAddress().getStreet());
        Assertions.assertEquals(expectedCompany.getAddress().getBuildingNumber(), actualCompany.getAddress().getBuildingNumber());
    }
}