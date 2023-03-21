package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AddressJpaRepositoryTest{
    @Autowired
    private AddressJpaRepository addressJpaRepository;

    @Test
    void testReadAddressEmpty(){
        final Address address = new Address("cz", "Praha", "16017", "Chaloupeckého", "1915");

        Optional<Address> returnedAddress = addressJpaRepository.readAddress(address);
        assertTrue(returnedAddress.isEmpty());
    }

    @Test
    void testReadAddress(){
        final Address address = new Address("cz", "Praha", "16017", "Chaloupeckého", "1915");

        addressJpaRepository.save(address);

        Optional<Address> returnedAddress = addressJpaRepository.readAddress(address);
        assertTrue(returnedAddress.isPresent());

        Assertions.assertEquals(address.getCountryCode(), returnedAddress.get().getCountryCode());
        Assertions.assertEquals(address.getCity(), returnedAddress.get().getCity());
        Assertions.assertEquals(address.getPostalCode(), returnedAddress.get().getPostalCode());
        Assertions.assertEquals(address.getStreet(), returnedAddress.get().getStreet());
        Assertions.assertEquals(address.getBuildingNumber(),
                                returnedAddress.get().getBuildingNumber());
    }
}