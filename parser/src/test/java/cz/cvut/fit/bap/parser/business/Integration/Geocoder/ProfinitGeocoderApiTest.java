package cz.cvut.fit.bap.parser.business.Integration.Geocoder;

import cz.cvut.fit.bap.parser.business.Geocoder.ProfinitGeocodingApi;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
    Integration test testing profinit api geocoding.
 */
@SpringBootTest
class ProfinitGeocoderApiTest{

    @Autowired
    private ProfinitGeocodingApi profinitGeocoderApi;

    @Test
    public void test(){
        //Čechova 225/10, 17000, Praha 7
        AddressDto addressDto = new AddressDto("CZ", "Praha", "17000", "Čechova", "10");
        Address expectedAddress = new Address("CZ", "Praha", "17000", "Čechova", "10",
                                              50.10070427864861, 14.42151999409848);
        Address actualAddress = profinitGeocoderApi.geocode(addressDto);

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