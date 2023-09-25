package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContractingAuthorityJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
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
class ContractingAuthorityServiceTest {
    @InjectMocks
    private ContractingAuthorityService contractingAuthorityService;

    @Mock
    private ContractingAuthorityJpaRepository contractingAuthorityJpaRepository;

    @Test
    void readByNameExisting(){
        String name = "name";
        ContractingAuthority contractingAuthority = new ContractingAuthority(name, new Address(), "url");
        when(contractingAuthorityJpaRepository.findContractingAuthorityByName(
                name)).thenReturn(Optional.of(contractingAuthority));
        Optional<ContractingAuthority> returnedContractorAuthority = contractingAuthorityService.readByName(name);

        assertTrue(returnedContractorAuthority.isPresent());
        assertEquals(contractingAuthority, returnedContractorAuthority.get());
        assertEquals(contractingAuthority.getName(), returnedContractorAuthority.get().getName());
        assertEquals(contractingAuthority.getAddress(), returnedContractorAuthority.get().getAddress());
    }

    @Test
    void readByNameNonExisting(){
        String name = "name";
        when(contractingAuthorityJpaRepository.findContractingAuthorityByName(name)).thenReturn(Optional.empty());
        Optional<ContractingAuthority> returnContractorAuthority = contractingAuthorityService.readByName(
                name);
        assertTrue(returnContractorAuthority.isEmpty());
    }
}