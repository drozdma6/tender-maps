package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContractorAuthorityJpaRepositoryTest{

    @Autowired
    private ContractorAuthorityJpaRepository contractorAuthorityJpaRepository;

//    private final Address testAddress = new Address("sk", "Bratislava", "16000",
//                                            "Bratislavska", "65");

    @Test
    void readContractorByName(){
        String name = "testName";
        String profile = "testProfile";
        ContractorAuthority contractorAuthority = new ContractorAuthority(name, profile, null);

        contractorAuthorityJpaRepository.save(contractorAuthority);

        Optional<ContractorAuthority> returnedContractor = contractorAuthorityJpaRepository.findContractorAuthorityByName(
                name);

        assertTrue(returnedContractor.isPresent());
        assertEquals(name, returnedContractor.get().getName());
        assertEquals(profile, returnedContractor.get().getProfile());

    }

    @Test
    public void readContractorByNameNonExisting(){
        String name = "testName";
        Optional<ContractorAuthority> returnedContractor = contractorAuthorityJpaRepository.findContractorAuthorityByName(
                name);
        assertTrue(returnedContractor.isEmpty());
    }
}