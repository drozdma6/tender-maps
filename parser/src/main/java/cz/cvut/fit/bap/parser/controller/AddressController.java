package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.converter.AddressDataToAddress;
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
    private final AddressDataToAddress addressDataToAddress;
    private final ProfinitGeocoder profinitGeocoder;
    private final GoogleGeocoder googleGeocoder;

    public AddressController(AddressService addressService, AddressDataToAddress addressDataToAddress, ProfinitGeocoder profinitGeocoder, GoogleGeocoder googleGeocoder){
        super(addressService);
        this.addressDataToAddress = addressDataToAddress;
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
     * Geocodes addressData. Uses profinit geocoding api for czech places. If google api key was defined uses
     * it for foreign places.
     *
     * @param addressData to be geocoded
     * @return geocoded address
     */
    public Address geocode(AddressData addressData){
        if(dtoIsIncomplete(addressData)){
            return addressDataToAddress.apply(addressData);
        }
        String country = addressData.country().toLowerCase();
        // Profinit geocoder for Czech places
        if(Objects.equals(country, "cz")){
            return profinitGeocoder.geocode(addressData);
        }
        // Google geocoder for foreign countries
        return googleGeocoder.geocode(addressData);
    }

    private boolean dtoIsIncomplete(AddressData addressData){
        return addressData.country() == null || addressData.buildingNumber() == null ||
                addressData.city() == null || addressData.street() == null ||
                addressData.postalCode() == null;
    }
}
