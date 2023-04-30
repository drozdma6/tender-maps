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
     * Creates new address entity from addressDto by setting country code, latitude and longitude
     *
     * @param addressDto which is supposed to be stored
     * @return address from database
     */
    public Address saveAddress(AddressDto addressDto){
        Optional<Address> addressOptional = service.readAddress(addressDto);
        if(addressOptional.isPresent()){
            return addressOptional.get();
        }
        if(dtoIsIncomplete(addressDto)){
            return service.create(addressDtoToAddress.apply(addressDto));
        }
        Address address;
        String country = addressDto.getCountry().toLowerCase();
        if(Objects.equals(country, "cz")){
            //profinit geocoder for czech places
            address = profinitGeocoding.geocode(addressDto);
        }else{
            //google geocoder for foreign countries
            address = googleGeocoding.geocode(addressDto);
        }
        return service.create(address);
    }

    private boolean dtoIsIncomplete(AddressDto addressDto){
        return addressDto.getCountry() == null || addressDto.getBuildingNumber() == null ||
                addressDto.getCity() == null || addressDto.getStreet() == null ||
                addressDto.getPostalCode() == null;
    }
}
