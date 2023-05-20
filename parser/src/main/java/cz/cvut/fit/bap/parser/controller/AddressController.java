package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.controller.Geocoder.GoogleGeocoding;
import cz.cvut.fit.bap.parser.controller.Geocoder.ProfinitGeocoding;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.converter.AddressDtoToAddress;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/*
    Controller for communication with address service
 */
@Component
public class AddressController extends AbstractController<AddressService>{
    private final AddressDtoToAddress addressDtoToAddress;
    private final ProfinitGeocoding profinitGeocoding;
    private final GoogleGeocoding googleGeocoding;

    public AddressController(AddressService addressService, AddressDtoToAddress addressDtoToAddress, ProfinitGeocoding profinitGeocoding, GoogleGeocoding googleGeocoding){
        super(addressService);
        this.addressDtoToAddress = addressDtoToAddress;
        this.profinitGeocoding = profinitGeocoding;
        this.googleGeocoding = googleGeocoding;
    }

    /**
     * Saves address
     *
     * @param address which is supposed to be stored
     * @return address from database
     */
    public Address saveAddress(Address address){
        Optional<Address> addressOptional = service.readAddress(address);
        return addressOptional.orElseGet(() -> service.create(address));
    }

    public Address geocode(AddressDto addressDto){
        if(dtoIsIncomplete(addressDto)){
            return addressDtoToAddress.apply(addressDto);
        }
        String country = addressDto.country().toLowerCase();
        if(Objects.equals(country, "cz")){
            //profinit geocoder for czech places
            return profinitGeocoding.geocode(addressDto);
        }else{
            //google geocoder for foreign countries
            return googleGeocoding.geocode(addressDto);
        }
    }

    private boolean dtoIsIncomplete(AddressDto addressDto){
        return addressDto.country() == null || addressDto.buildingNumber() == null ||
                addressDto.city() == null || addressDto.street() == null ||
                addressDto.postalCode() == null;
    }
}
