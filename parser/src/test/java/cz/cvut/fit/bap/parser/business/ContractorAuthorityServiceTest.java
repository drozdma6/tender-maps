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
        String name = "name";
        ContractorAuthority contractorAuthority = new ContractorAuthority(name, new Address(), "url");
        when(contractorAuthorityJpaRepository.findContractorAuthorityByName(
                name)).thenReturn(Optional.of(contractorAuthority));
        Optional<ContractorAuthority> returnedContractorAuthority = contractorAuthorityService.readByName(name);

        assertTrue(returnedContractorAuthority.isPresent());
        assertEquals(contractorAuthority, returnedContractorAuthority.get());
        assertEquals(contractorAuthority.getName(), returnedContractorAuthority.get().getName());
        assertEquals(contractorAuthority.getAddress(), returnedContractorAuthority.get().getAddress());
    }

    @Test
    void readByNameNonExisting(){
        String name = "name";
        when(contractorAuthorityJpaRepository.findContractorAuthorityByName(name)).thenReturn(Optional.empty());
        Optional<ContractorAuthority> returnContractorAuthority = contractorAuthorityService.readByName(
                name);
        assertTrue(returnContractorAuthority.isEmpty());
    }
}