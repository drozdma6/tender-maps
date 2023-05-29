package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContractorAuthorityJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractorAuthorityServiceTest{
    @InjectMocks
    private ContractorAuthorityService contractorAuthorityService;

    @Mock
    private ContractorAuthorityJpaRepository contractorAuthorityJpaRepository;

    @Test
    void readByNameExisting(){
        String profile = "profile";
        ContractorAuthority contractorAuthority = new ContractorAuthority("name", profile,
                new Address(), "url");
        when(contractorAuthorityJpaRepository.findContractorAuthorityByProfile(
                profile)).thenReturn(Optional.of(contractorAuthority));
        Optional<ContractorAuthority> returnedContractorAuthority = contractorAuthorityService.readByProfile(
                profile);
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
        String profile = "testProfile";
        when(contractorAuthorityJpaRepository.findContractorAuthorityByProfile(profile)).thenReturn(
                Optional.empty());
        Optional<ContractorAuthority> returnContractorAuthority = contractorAuthorityService.readByProfile(
                profile);
        assertTrue(returnContractorAuthority.isEmpty());
    }
}