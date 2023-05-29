package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.TestConfigurationClass;
import cz.cvut.fit.bap.parser.domain.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfigurationClass.class)
public class CompanyJpaRepositoryTest{
    @Autowired
    private CompanyJpaRepository companyJpaRepository;

    @Test
    public void testFindCompanyByName(){
        String companyName = "testName";
        Company company = new Company(companyName, null, "organisationId");

        companyJpaRepository.save(company);

        Optional<Company> foundCompany = companyJpaRepository.findCompanyByName(companyName);

        assertTrue(foundCompany.isPresent());
        assertEquals(companyName, foundCompany.get().getName());
    }

    @Test
    public void testFindCompanyByNameNonExisting(){
        String companyName = "testName";
        Optional<Company> foundCompany = companyJpaRepository.findCompanyByName(companyName);

        assertTrue(foundCompany.isEmpty());
    }
}