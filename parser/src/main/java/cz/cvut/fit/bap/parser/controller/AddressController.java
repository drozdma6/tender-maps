package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.converter.AddressDtoToAddress;
import cz.cvut.fit.bap.parser.controller.geocoder.GoogleGeocoder;
import cz.cvut.fit.bap.parser.controller.geocoder.ProfinitGeocoder;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/*
    Controller for addresses
 */
@Component
public class AddressController extends AbstractController<AddressService,Address,Long>{
    private final AddressDtoToAddress addressDtoToAddress;
    private final ProfinitGeocoder profinitGeocoder;
    private final GoogleGeocoder googleGeocoder;

    public AddressController(AddressService addressService, AddressDtoToAddress addressDtoToAddress, ProfinitGeocoder profinitGeocoder, GoogleGeocoder googleGeocoder){
        super(addressService);
        this.addressDtoToAddress = addressDtoToAddress;
        this.profinitGeocoder = profinitGeocoder;
        this.googleGeocoder = googleGeocoder;
    }

    /**
     * Saves address
     *
     * @param address which is supposed to be stored
     * @return address from database
     */
    @Override
    public Address save(Address address){
        Optional<Address> addressOptional = service.readAddress(address);
        return addressOptional.orElseGet(() -> super.save(address));
    }

    /**
     * Geocodes addressDto. Uses profinit geocoding api for czech places. If google api key was defined uses
     * it for foreign places.
     *
     * @param addressDto to be geocoded
     * @return geocoded address
     */
    public Address geocode(AddressDto addressDto){
        if(dtoIsIncomplete(addressDto)){
            return addressDtoToAddress.apply(addressDto);
        }
        String country = addressDto.country().toLowerCase();
        // Profinit geocoder for Czech places
        if(Objects.equals(country, "cz")){
            return profinitGeocoder.geocode(addressDto);
        }
        // Google geocoder for foreign countries
        return googleGeocoder.geocode(addressDto);
    }

    private boolean dtoIsIncomplete(AddressDto addressDto){
        return addressDto.country() == null || addressDto.buildingNumber() == null ||
                addressDto.city() == null || addressDto.street() == null ||
                addressDto.postalCode() == null;
    }
}
