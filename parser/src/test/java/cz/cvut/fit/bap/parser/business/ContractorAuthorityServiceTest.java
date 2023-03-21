package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContractorAuthorityJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class ContractorAuthorityServiceTest{
    @Autowired
    private ContractorAuthorityService contractorAuthorityService;

    @MockBean
    private ContractorAuthorityJpaRepository contractorAuthorityJpaRepository;

    @Test
    void readByNameExisting(){
        String authorityName = "testContractor";
        ContractorAuthority contractorAuthority = new ContractorAuthority(authorityName, "",
                                                                          new Address());
        when(contractorAuthorityJpaRepository.findContractorAuthorityByName(
                authorityName)).thenReturn(Optional.of(contractorAuthority));
        Optional<ContractorAuthority> returnedContractorAuthority = contractorAuthorityService.readByName(
                authorityName);
        assertTrue(returnedContractorAuthority.isPresent());
        assertEquals(contractorAuthority, returnedContractorAuthority.get());
        assertEquals(contractorAuthority.getName(), returnedContractorAuthority.get().getName());
        assertEquals(contractorAuthority.getProfile(),
                     returnedContractorAuthority.get().getProfile());
        assertEquals(contractorAuthority.getAddress(),
                     returnedContractorAuthority.get().getAddress());
    }

    @Test
    void readByNameNonExisting(){
        String name = "testName";
        when(contractorAuthorityJpaRepository.findContractorAuthorityByName(name)).thenReturn(
                Optional.empty());
        Optional<ContractorAuthority> returnContractorAuthority = contractorAuthorityService.readByName(
                name);
        assertTrue(returnContractorAuthority.isEmpty());
    }
}