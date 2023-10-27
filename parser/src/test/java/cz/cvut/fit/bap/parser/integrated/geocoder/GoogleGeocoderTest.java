package cz.cvut.fit.bap.parser.integrated.geocoder;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.converter.AddressDataToAddress;
import cz.cvut.fit.bap.parser.controller.geocoder.GoogleGeocoder;
import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
    Integration test testing google api geocoding. Google API key is required.
 */
@SpringBootTest
class GoogleGeocoderTest{
    @Autowired
    private GoogleGeocoder googleGeocoder;

    @Test
    public void geocode(){
        AddressData addressData = new AddressData("Česká republika", "Praha", "17000", "Čechova",
                "10");
        Address expectedAddress = new Address("CZ", "Praha", "17000", "Čechova", "10", 50.1006926,
                14.4215078);
        Address actualAddress = googleGeocoder.geocode(addressData);

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

    @Test
    public void geocodeMissingApiKey(){
        AddressDataToAddress addressDataToAddress = new AddressDataToAddress();
        GoogleGeocoder googleGeocoder = new GoogleGeocoder("", addressDataToAddress);
        AddressData addressData = new AddressData("CZ", "Praha", "17000", "Čechova",
                "10");
        Address expectedAddress = new Address("CZ", "Praha", "17000", "Čechova",
                "10");
        Address actualAddress = googleGeocoder.geocode(addressData);

        Assertions.assertEquals(expectedAddress.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(expectedAddress.getCity(), actualAddress.getCity());
        Assertions.assertEquals(expectedAddress.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(expectedAddress.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(expectedAddress.getBuildingNumber(),
                actualAddress.getBuildingNumber());
        Assertions.assertNull(actualAddress.getLatitude());
        Assertions.assertNull(actualAddress.getLongitude());
    }
}
