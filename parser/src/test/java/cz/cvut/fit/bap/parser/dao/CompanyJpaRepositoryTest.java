package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CompanyJpaRepositoryTest{

    @Autowired
    private CompanyJpaRepository companyJpaRepository;

    @Test
    public void testFindCompanyByName(){
        String companyName = "testName";
        Company company = new Company(companyName, null);

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