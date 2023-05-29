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
    void readContractorByProfile(){
        String name = "testName";
        String profile = "testProfile";
        ContractorAuthority contractorAuthority = new ContractorAuthority(name, profile, null, "url");

        contractorAuthorityJpaRepository.save(contractorAuthority);

        Optional<ContractorAuthority> returnedContractor = contractorAuthorityJpaRepository.findContractorAuthorityByProfile(
                profile);

        assertTrue(returnedContractor.isPresent());
        assertEquals(name, returnedContractor.get().getName());
        assertEquals(profile, returnedContractor.get().getProfile());
    }

    @Test
    public void readContractorByProfileNonExisting(){
        String profile = "testProfile";
        Optional<ContractorAuthority> returnedContractor = contractorAuthorityJpaRepository.findContractorAuthorityByProfile(
                profile);
        assertTrue(returnedContractor.isEmpty());
    }
}