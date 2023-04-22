package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.business.Geocoder.GoogleGeocodingApi;
import cz.cvut.fit.bap.parser.business.Geocoder.ProfinitGeocodingApi;
import cz.cvut.fit.bap.parser.dao.AddressJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
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
    private GoogleGeocodingApi googleGeocodingApi;

    @MockBean
    private ProfinitGeocodingApi profinitGeocodingApi;

    @BeforeEach
    void setUp(){
        when(addressJpaRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void createExisting(){
        final Address geocodedAddress = new Address("SK", "Bratislava", "16000", "Bratislavska",
                                                    "65", 50.0799251615831, 14.394026044199839);

        final AddressDto existingAddressDto = new AddressDto("Slovensko", "Bratislava", "16000",
                                                             "Bratislavska", "65");

        when(addressJpaRepository.readAddress(existingAddressDto)).thenReturn(
                Optional.of(geocodedAddress));
        geocodedAddress.setId(1L);
        when(addressJpaRepository.existsById(geocodedAddress.getId())).thenReturn(true);

        Address actualAddress = addressService.create(existingAddressDto);
        verify(profinitGeocodingApi, never()).geocode(any());
        verify(googleGeocodingApi, never()).geocode(any());

        Assertions.assertEquals(geocodedAddress, actualAddress);
        Assertions.assertEquals(geocodedAddress.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(geocodedAddress.getCity(), actualAddress.getCity());
        Assertions.assertEquals(geocodedAddress.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(geocodedAddress.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(geocodedAddress.getBuildingNumber(),
                                actualAddress.getBuildingNumber());
        verify(addressJpaRepository, never()).save(geocodedAddress);

    }

    @Test
    void testNonExistingForeign(){
        final Address geocodedAddress = new Address("SK", "Bratislava", "16000", "Bratislavska",
                                                    "65", 50.0799251615831, 14.394026044199839);

        final AddressDto nonExistingAddress = new AddressDto("Slovensko", "Bratislava", "16000",
                                                             "Bratislavska", "65");
        when(addressJpaRepository.existsById(any())).thenReturn(false);
        when(addressJpaRepository.readAddress(nonExistingAddress)).thenReturn(Optional.empty());
        when(googleGeocodingApi.geocode(any(AddressDto.class))).thenReturn(geocodedAddress);


        Address actualAddress = addressService.create(nonExistingAddress);
        verify(googleGeocodingApi, times(1)).geocode(any());
        verify(profinitGeocodingApi, never()).geocode(any());


        Assertions.assertEquals(geocodedAddress.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(geocodedAddress.getCity(), actualAddress.getCity());
        Assertions.assertEquals(geocodedAddress.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(geocodedAddress.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(geocodedAddress.getBuildingNumber(),
                                actualAddress.getBuildingNumber());
        Assertions.assertEquals(geocodedAddress.getLatitude(), actualAddress.getLatitude());
        Assertions.assertEquals(geocodedAddress.getLongitude(), actualAddress.getLongitude());

        verify(addressJpaRepository, times(1)).save(geocodedAddress);
    }

    @Test
    void testNonExistingCzech(){
        Address geocodedAddress = new Address("CZ", "Bratislava", "16000", "Bratislavska", "65",
                                              50.0799251615831, 14.394026044199839);

        final AddressDto nonExistingAddress = new AddressDto("Česká republika", "Bratislava",
                                                             "16000", "Bratislavska", "65");

        when(addressJpaRepository.existsById(any())).thenReturn(false);

        when(addressJpaRepository.readAddress(nonExistingAddress)).thenReturn(Optional.empty());
        when(profinitGeocodingApi.geocode(any(AddressDto.class))).thenReturn(geocodedAddress);


        Address actualAddress = addressService.create(nonExistingAddress);
        verify(profinitGeocodingApi, times(1)).geocode(any());
        verify(googleGeocodingApi, never()).geocode(any());


        Assertions.assertEquals(geocodedAddress.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(geocodedAddress.getCity(), actualAddress.getCity());
        Assertions.assertEquals(geocodedAddress.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(geocodedAddress.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(geocodedAddress.getBuildingNumber(),
                                actualAddress.getBuildingNumber());
        Assertions.assertEquals(geocodedAddress.getLatitude(), actualAddress.getLatitude());
        Assertions.assertEquals(geocodedAddress.getLongitude(), actualAddress.getLongitude());

        verify(addressJpaRepository, times(1)).save(geocodedAddress);
    }

    @Test
    void testIncompleteAddress(){
        final AddressDto nonExistingAddress = new AddressDto("Česká republika", null, null,
                                                             "Bratislavska", "65");

        Address address = new Address(null, null, null, "Bratislavska", "65");


        when(addressJpaRepository.existsById(any())).thenReturn(false);

        when(addressJpaRepository.readAddress(nonExistingAddress)).thenReturn(Optional.empty());

        Address actualAddress = addressService.create(nonExistingAddress);
        verify(profinitGeocodingApi, never()).geocode(any());
        verify(googleGeocodingApi, never()).geocode(any());


        Assertions.assertEquals(address.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(address.getCity(), actualAddress.getCity());
        Assertions.assertEquals(address.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(address.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(address.getBuildingNumber(), actualAddress.getBuildingNumber());
        Assertions.assertEquals(address.getLatitude(), actualAddress.getLatitude());
        Assertions.assertEquals(address.getLongitude(), actualAddress.getLongitude());

        verify(addressJpaRepository, times(1)).save(any(Address.class));
    }

    @Test
    void unsupportedCreate(){
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> addressService.create(new Address()));
    }
}