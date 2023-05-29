package cz.cvut.fit.bap.parser.controller.Geocoder;

import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
    Integration test testing google api geocoding.
 */
@SpringBootTest
class GoogleGeocodingTest{
    @Autowired
    private GoogleGeocoding googleGeocoding;

    @Test
    public void geocode(){
        AddressDto addressDto = new AddressDto("Česká republika", "Praha", "17000", "Čechova",
                "10");
        Address expectedAddress = new Address("CZ", "Praha", "17000", "Čechova", "10", 50.1006926,
                14.4215078);
        Address actualAddress = googleGeocoding.geocode(addressDto);

        Assertions.assertEquals(expectedAddress.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(expectedAddress.getCity(), actualAddress.getCity());
        Assertions.assertEquals(expectedAddress.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(expectedAddress.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(expectedAddress.getBuildingNumber(),
                actualAddress.getBuildingNumber());
        double epsilon = 0.00001;
        Assertions.assertEquals(expectedAddress.getLongitude(), actualAddress.getLongitude(),
                epsilon);
        Assertions.assertEquals(expectedAddress.getLatitude(), actualAddress.getLatitude(),
                epsilon);
    }
}
