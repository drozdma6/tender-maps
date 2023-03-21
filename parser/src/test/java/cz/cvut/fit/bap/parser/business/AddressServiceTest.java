package cz.cvut.fit.bap.parser.business;

import com.google.maps.model.GeocodingResult;
import cz.cvut.fit.bap.parser.dao.AddressJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.Mockito.*;


@SpringBootTest
class AddressServiceTest{
    @Autowired
    private AddressService addressService;

    @MockBean
    private AddressJpaRepository addressJpaRepository;

    @MockBean
    private GeocodingApiClient geocodingApiClient;

    @BeforeEach
    void setUp(){
        GeocodingResult[] testGeocodingResults = new GeocodingResult[]{};
        Double testLatitude = 50.0799251615831;
        Double testLongitude = 14.394026044199839;

        when(geocodingApiClient.geocode(any(Address.class))).thenReturn(testGeocodingResults);
        when(geocodingApiClient.getLat(any())).thenReturn(testLatitude);
        when(geocodingApiClient.getLat(any())).thenReturn(testLongitude);
        when(addressJpaRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));
    }


    @Test
    void createExisting(){
        final Address existingAddress = new Address("cz", "Praha", "16017", "Chaloupeckého",
                                                    "1915");
        existingAddress.setId(1L);

        when(addressJpaRepository.existsById(existingAddress.getId())).thenReturn(true);
        when(addressJpaRepository.readAddress(existingAddress)).thenReturn(
                Optional.of(existingAddress));

        Address actualAddress = addressService.create(existingAddress);
        verify(geocodingApiClient, never()).geocode(any());
        verify(geocodingApiClient, never()).getLat(any());
        verify(geocodingApiClient, never()).getLng(any());

        Assertions.assertEquals(existingAddress, actualAddress);
        Assertions.assertEquals(existingAddress.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(existingAddress.getCity(), actualAddress.getCity());
        Assertions.assertEquals(existingAddress.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(existingAddress.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(existingAddress.getBuildingNumber(),
                                actualAddress.getBuildingNumber());
        verify(addressJpaRepository, never()).save(existingAddress);

    }

    @Test
    void testNonExisting(){
        final Address nonExistingAddress = new Address("sk", "Bratislava", "16000", "Bratislavska",
                                                       "65");
        when(addressJpaRepository.existsById(nonExistingAddress.getId())).thenReturn(false);

        when(addressJpaRepository.readAddress(nonExistingAddress)).thenReturn(Optional.empty());

        Address actualAddress = addressService.create(nonExistingAddress);
        verify(geocodingApiClient, times(1)).geocode(any());
        verify(geocodingApiClient, times(1)).getLat(any());
        verify(geocodingApiClient, times(1)).getLng(any());

        Assertions.assertEquals(nonExistingAddress.getCountryCode(),
                                actualAddress.getCountryCode());
        Assertions.assertEquals(nonExistingAddress.getCity(), actualAddress.getCity());
        Assertions.assertEquals(nonExistingAddress.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(nonExistingAddress.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(nonExistingAddress.getBuildingNumber(),
                                actualAddress.getBuildingNumber());

        verify(addressJpaRepository, times(1)).save(nonExistingAddress);

    }
}