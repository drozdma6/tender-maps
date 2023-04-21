package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
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
        final AddressDto addressDto = new AddressDto("SK", "65", "Bratislava", "Bratislavska",
                                                     "16000");
        Optional<Address> returnedAddress = addressJpaRepository.readAddress(addressDto);
        assertTrue(returnedAddress.isEmpty());
    }

    @Test
    void testReadAddress(){
        final AddressDto addressDto = new AddressDto("SK", "Bratislava", "16000", "Bratislavska",
                                                     "65");

        final Address address = new Address("SK", "Bratislava", "16000", "Bratislavska", "65");

        addressJpaRepository.save(address);

        Optional<Address> returnedAddress = addressJpaRepository.readAddress(addressDto);
        assertTrue(returnedAddress.isPresent());

        Assertions.assertEquals(address.getCountryCode(), returnedAddress.get().getCountryCode());
        Assertions.assertEquals(address.getCity(), returnedAddress.get().getCity());
        Assertions.assertEquals(address.getPostalCode(), returnedAddress.get().getPostalCode());
        Assertions.assertEquals(address.getStreet(), returnedAddress.get().getStreet());
        Assertions.assertEquals(address.getBuildingNumber(),
                                returnedAddress.get().getBuildingNumber());
    }
}