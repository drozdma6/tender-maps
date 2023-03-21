package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.CompanyJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CompanyServiceTest{
    @Autowired
    private CompanyService companyService;

    @MockBean
    private CompanyJpaRepository companyJpaRepository;

    @Test
    void createExistingId(){
        Company company = new Company("testCompany", new Address());
        company.setId(1L);
        when(companyJpaRepository.existsById(company.getId())).thenReturn(true);
        when(companyJpaRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));
        assertThrows(RuntimeException.class, () -> companyService.create(company));
        verify(companyJpaRepository, never()).save(company);
    }

    @Test
    void createNonExistingCompany(){
        Company company = new Company("testCompany", new Address());
        company.setId(1L);
        when(companyJpaRepository.existsById(company.getId())).thenReturn(false);
        when(companyJpaRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));
        companyService.create(company);
        verify(companyJpaRepository, times(1)).save(company);
    }

    @Test
    void readByNameExisting(){
        String companyName = "testCompany";
        Company company = new Company(companyName, new Address());
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