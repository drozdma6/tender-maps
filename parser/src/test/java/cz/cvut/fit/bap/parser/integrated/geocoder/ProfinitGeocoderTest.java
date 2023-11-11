package cz.cvut.fit.bap.parser.integrated.geocoder;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.geocoder.ProfinitGeocoder;
import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
    Integration test testing profinit api geocoding. Api key is required for this test.
 */
@SpringBootTest
class ProfinitGeocoderTest{

    @Autowired
    private ProfinitGeocoder profinitGeocoderApi;

    @Test
    public void test(){
        //Čechova 225/10, 17000, Praha 7
        AddressData addressData = new AddressData("Česká republika", "Praha", "17000", "Čechova",
                "10", "1", null);
        Address expectedAddress = new Address("CZ", "Praha", "17000", "Čechova", "10", "1",
                50.10070427864861, 14.42151999409848);
        Address actualAddress = profinitGeocoderApi.geocode(addressData);

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