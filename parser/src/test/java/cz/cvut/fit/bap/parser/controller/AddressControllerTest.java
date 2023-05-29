package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.controller.Geocoder.GoogleGeocoding;
import cz.cvut.fit.bap.parser.controller.Geocoder.ProfinitGeocoding;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.converter.AddressDtoToAddress;
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
    private ProfinitGeocoding profinitGeocoding;

    @Mock
    private GoogleGeocoding googleGeocoding;

    @Mock
    private AddressService addressService;

    @Mock
    private AddressDtoToAddress addressDtoToAddress;

    @Test
    void saveAddressExistingAddress(){
        Address address = new Address("SK", "Bratislava", "16000", "Bratislavska",
                "65", 50.0799251615831, 14.394026044199839);
        when(addressService.readAddress(address)).thenReturn(Optional.of(address));
        Address actualAddress = addressController.saveAddress(address);
        verify(addressService, never()).create(any(Address.class));
        Assertions.assertEquals(address.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(address.getCity(), actualAddress.getCity());
        Assertions.assertEquals(address.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(address.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(address.getBuildingNumber(), actualAddress.getBuildingNumber());
    }

    @Test
    void saveAddressNewAddress(){

        Address address = new Address("SK", "Bratislava", "16000", "Bratislavska",
                "65", 50.0799251615831, 14.394026044199839);
        when(addressService.readAddress(address)).thenReturn(Optional.empty());
        when(addressService.create(any(Address.class))).thenAnswer(i -> i.getArgument(0));
        Address actualAddress = addressController.saveAddress(address);
        verify(addressService).create(any(Address.class));
        Assertions.assertEquals(address.getCountryCode(), actualAddress.getCountryCode());
        Assertions.assertEquals(address.getCity(), actualAddress.getCity());
        Assertions.assertEquals(address.getPostalCode(), actualAddress.getPostalCode());
        Assertions.assertEquals(address.getStreet(), actualAddress.getStreet());
        Assertions.assertEquals(address.getBuildingNumber(), actualAddress.getBuildingNumber());
    }

    @Test
    void geocodeForeign(){
        AddressDto addressDto = new AddressDto("SK", "Bratislava", "16000", "Bratislavska", "65");
        addressController.geocode(addressDto);
        verify(profinitGeocoding, never()).geocode(addressDto);
        verify(googleGeocoding).geocode(addressDto);
    }

    @Test
    void geocode(){
        AddressDto addressDto = new AddressDto("CZ", "Praha", "16000", "Bratislavska", "65");
        addressController.geocode(addressDto);
        verify(googleGeocoding, never()).geocode(addressDto);
        verify(profinitGeocoding).geocode(addressDto);
    }

    @Test
    void geocodeIncomplete(){
        AddressDto addressDto = new AddressDto("CZ", "Praha", null, "Bratislavska", "65");
        addressController.geocode(addressDto);
        verify(googleGeocoding, never()).geocode(addressDto);
        verify(profinitGeocoding, never()).geocode(addressDto);
        verify(addressDtoToAddress).apply(addressDto);
    }
}