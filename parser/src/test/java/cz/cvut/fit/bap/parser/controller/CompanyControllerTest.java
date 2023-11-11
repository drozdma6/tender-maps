package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
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
    private CompanyService companyService;

    @Test
    void saveCompanyNewCompany(){
        Company company = new Company("Test company", new Address(), "organisationId", "VATIdNumber");
        when(companyService.readByName(company.getName())).thenReturn(Optional.empty());
        when(companyService.create(company)).thenAnswer(i -> i.getArgument(0));
        when(addressController.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        Company savedCompany = companyController.save(company);
        verify(addressController).save(any(Address.class));
        verify(companyService).create(any(Company.class));
        Assertions.assertEquals(company.getName(), savedCompany.getName());
        Assertions.assertEquals(company.getOrganisationId(), savedCompany.getOrganisationId());
    }

    @Test
    void saveCompanyExisting(){
        Company company = new Company("Test company", new Address(), "organisationId", "VATIdNumber");
        when(companyService.readByName(company.getName())).thenReturn(Optional.of(company));

        Company savedCompany = companyController.save(company);
        verify(addressController, never()).save(any(Address.class));
        verify(companyService, never()).create(any(Company.class));
        Assertions.assertEquals(company.getName(), savedCompany.getName());
        Assertions.assertEquals(company.getOrganisationId(), savedCompany.getOrganisationId());
    }

    @Test
    void getCompany() {
        AddressData addressData = new AddressData("SK", null, "16000", "Bratislavska", "65", "1", null);
        Address address = new Address("SK", null, "16000", "Bratislavska",
                "65", "1", null, null);
        String name = "name";
        String organisationId = "organisationId";
        String VATIdNumber = "VATIdNumber";
        Company expectedCompany = new Company(name, address, organisationId, VATIdNumber);
        when(addressController.geocode(any())).thenReturn(address);
        Company actualCompany = companyController.buildCompany(name, addressData, organisationId, VATIdNumber);
        Assertions.assertEquals(expectedCompany.getName(), actualCompany.getName());
        Assertions.assertEquals(expectedCompany.getOrganisationId(), actualCompany.getOrganisationId());
        Assertions.assertEquals(expectedCompany.getVATIdNumber(), actualCompany.getVATIdNumber());
        Assertions.assertEquals(expectedCompany.getAddress().getLatitude(), actualCompany.getAddress().getLatitude());
        Assertions.assertEquals(expectedCompany.getAddress().getLongitude(), actualCompany.getAddress().getLongitude());
    }
}