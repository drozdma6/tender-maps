package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.TestConfigurationClass;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfigurationClass.class)
class ContractingAuthorityJpaRepositoryTest {

    @Autowired
    private ContractingAuthorityJpaRepository contractingAuthorityJpaRepository;

    @Test
    void findContractingAuthorityByName() {
        String name = "testName";
        ContractingAuthority contractingAuthority = new ContractingAuthority(name, null, "url");

        contractingAuthorityJpaRepository.save(contractingAuthority);

        Optional<ContractingAuthority> returnedContractor = contractingAuthorityJpaRepository.findContractingAuthorityByName(name);

        assertTrue(returnedContractor.isPresent());
        assertEquals(name, returnedContractor.get().getName());
        assertEquals("url", returnedContractor.get().getUrl());
    }

    @Test
    public void findContractingAuthorityByNameNonExisting() {
        String name = "name";
        Optional<ContractingAuthority> returnedContractor = contractingAuthorityJpaRepository.findContractingAuthorityByName(name);
        assertTrue(returnedContractor.isEmpty());
    }
}