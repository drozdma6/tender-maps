package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.TestConfigurationClass;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfigurationClass.class)
class ContractorAuthorityJpaRepositoryTest{

    @Autowired
    private ContractorAuthorityJpaRepository contractorAuthorityJpaRepository;

    @Test
    void findContractorAuthorityByName(){
        String name = "testName";
        ContractorAuthority contractorAuthority = new ContractorAuthority(name, null, "url");

        contractorAuthorityJpaRepository.save(contractorAuthority);

        Optional<ContractorAuthority> returnedContractor = contractorAuthorityJpaRepository.findContractorAuthorityByName(name);

        assertTrue(returnedContractor.isPresent());
        assertEquals(name, returnedContractor.get().getName());
        assertEquals("url", returnedContractor.get().getUrl());
    }

    @Test
    public void findContractorAuthorityByNameNonExisting(){
        String name = "name";
        Optional<ContractorAuthority> returnedContractor = contractorAuthorityJpaRepository.findContractorAuthorityByName(name);
        assertTrue(returnedContractor.isEmpty());
    }
}