package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.geocoder.GoogleGeocoder;
import cz.cvut.fit.bap.parser.controller.geocoder.ProfinitGeocoder;
import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest{
    @InjectMocks
    private AddressController addressController;

    @Mock
    private ProfinitGeocoder profinitGeocoder;

    @Mock
    private GoogleGeocoder googleGeocoder;

    @Mock
    private AddressService addressService;

    @Test
    void saveAddressExistingAddress(){
        Address address = new Address("SK", "Bratislava", "16000", "Bratislavska",
                "65", "1", 50.0799251615831, 14.394026044199839);
        when(addressService.readAddress(address)).thenReturn(Optional.of(address));
        Address actualAddress = addressController.save(address);
        verify(addressService, never()).create(any(Address.class));
        Assertions.assertEquals(address.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(address.getCity(), actualAddress.getCity());
        Assertions.assertEquals(address.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(address.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(address.getBuildingNumber(), actualAddress.getBuildingNumber());
        Assertions.assertEquals(address.getLandRegistryNumber(), actualAddress.getLandRegistryNumber());
        Assertions.assertEquals(address.getLandRegistryNumber(), actualAddress.getLandRegistryNumber());
        Assertions.assertEquals(address.getLatitude(), actualAddress.getLatitude());
        Assertions.assertEquals(address.getLongitude(), actualAddress.getLongitude());
    }

    @Test
    void saveAddressNewAddress(){
        Address address = new Address("SK", "Bratislava", "16000", "Bratislavska",
                "65", "1", 50.0799251615831, 14.394026044199839);
        when(addressService.readAddress(address)).thenReturn(Optional.empty());
        when(addressService.create(any(Address.class))).thenAnswer(i -> i.getArgument(0));
        Address actualAddress = addressController.save(address);
        verify(addressService).create(any(Address.class));
        Assertions.assertEquals(address.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(address.getCity(), actualAddress.getCity());
        Assertions.assertEquals(address.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(address.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(address.getBuildingNumber(), actualAddress.getBuildingNumber());
        Assertions.assertEquals(address.getLandRegistryNumber(), actualAddress.getLandRegistryNumber());
        Assertions.assertEquals(address.getLandRegistryNumber(), actualAddress.getLandRegistryNumber());
        Assertions.assertEquals(address.getLatitude(), actualAddress.getLatitude());
        Assertions.assertEquals(address.getLongitude(), actualAddress.getLongitude());
    }

    @Test
    void geocodeForeign(){
        AddressData addressData = new AddressData("Slovensko", "Bratislava", "16000", "Bratislavska", "65", "1", null);
        addressController.geocode(addressData);
        verify(profinitGeocoder, never()).geocode(addressData);
        verify(googleGeocoder).geocode(addressData);
    }

    @Test
    void geocode(){
        AddressData addressData = new AddressData("Česká republika", "Praha", "16000", "Bratislavska", "65", "1", null);
        addressController.geocode(addressData);
        verify(googleGeocoder, never()).geocode(addressData);
        verify(profinitGeocoder).geocode(addressData);
    }

    @Test
    void geocodeCountryCode() {
        AddressData addressData = new AddressData(null, "Praha", "16000", "Bratislavska", "65", "1", "CZ");
        addressController.geocode(addressData);
        verify(googleGeocoder, never()).geocode(addressData);
        verify(profinitGeocoder).geocode(addressData);
    }

    @Test
    void geocodeIncomplete(){
        AddressData addressData = new AddressData(null, null, "16000", "Bratislavska", "65", "1", "SK");
        Address expectedAddres = new Address("SK", null, "16000", "Bratislavska",
                "65", "1", null, null);
        Address actualAddress = addressController.geocode(addressData);
        verify(googleGeocoder, never()).geocode(addressData);
        verify(profinitGeocoder, never()).geocode(addressData);
        Assertions.assertEquals(expectedAddres, actualAddress);
    }


    @Test
    void geocodeSavedAddress() {
        AddressData addressData = new AddressData("Česká republika", "Praha", "16000", "Bratislavska", "65", "1", null);
        Address expectedAddress = new Address("CZ", "Praha", "16000", "Bratislavska",
                "65", "1", 50.0799251615831, 14.394026044199839);
        when(addressService.readAddress(any())).thenAnswer(i -> Optional.of(i.getArgument(0)));
        Address actualAddress = addressController.geocode(addressData);
        verify(googleGeocoder, never()).geocode(addressData);
        verify(profinitGeocoder, never()).geocode(addressData);
        Assertions.assertEquals(expectedAddress, actualAddress);
    }
}