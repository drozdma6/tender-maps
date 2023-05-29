package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.CompanyJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest{
    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyJpaRepository companyJpaRepository;

    @Test
    void createNonExistingCompany(){
        Company company = new Company("testCompany", new Address(), "organisationId");
        company.setId(1L);
        when(companyJpaRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));
        Company savedCompany = companyService.create(company);
        verify(companyJpaRepository, times(1)).save(company);
        Assertions.assertEquals(company.getName(), savedCompany.getName());
        Assertions.assertEquals(company.getOrganisationId(), savedCompany.getOrganisationId());
    }

    @Test
    void readByNameExisting(){
        String companyName = "testCompany";
        Company company = new Company(companyName, new Address(), "organisationId");
        when(companyJpaRepository.findCompanyByName(companyName)).thenReturn(Optional.of(company));
        Optional<Company> receivedCompany = companyService.readByName(companyName);
        assertTrue(receivedCompany.isPresent());
        assertEquals(company, receivedCompany.get());
        assertEquals(company.getName(), receivedCompany.get().getName());
        assertEquals(company.getAddress(), receivedCompany.get().getAddress());
    }


    @Test
    void readByNameNonExisting(){
        String companyName = "testCompany";
        when(companyJpaRepository.findCompanyByName(companyName)).thenReturn(Optional.empty());
        Optional<Company> receivedCompany = companyService.readByName(companyName);
        assertTrue(receivedCompany.isEmpty());
    }
}