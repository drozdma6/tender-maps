package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.AddressJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest{
    @InjectMocks
    private AddressService addressService;
    @Mock
    private AddressJpaRepository addressJpaRepository;

    @Test
    void readAddressExisting(){
        Address existingAddress = new Address();
        when(addressJpaRepository.readAddress(existingAddress)).thenReturn(Optional.of(existingAddress));
        Optional<Address> actualAddress = addressService.readAddress(existingAddress);
        Assertions.assertTrue(actualAddress.isPresent());
        Assertions.assertEquals(existingAddress, actualAddress.get());
    }

    @Test
    void readAddressNonExisting(){
        Address existingAddress = new Address();
        when(addressJpaRepository.readAddress(existingAddress)).thenReturn(Optional.empty());
        Optional<Address> actualAddress = addressService.readAddress(existingAddress);
        Assertions.assertTrue(actualAddress.isEmpty());
    }
}