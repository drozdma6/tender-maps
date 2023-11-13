package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.controller.builder.AddressBuilder;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
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
public class AddressController extends AbstractController<AddressService, Address, Long> {
    private final ProfinitGeocoder profinitGeocoder;
    private final GoogleGeocoder googleGeocoder;

    public AddressController(AddressService addressService, ProfinitGeocoder profinitGeocoder, GoogleGeocoder googleGeocoder) {
        super(addressService);
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
    public Address save(Address address) {
        Optional<Address> addressOptional = service.readAddress(address);
        return addressOptional.orElseGet(() -> super.save(address));
    }

    /**
     * Geocodes addressData. Uses profinit geocoding api for czech places. Google geocoding api for foreign places.
     *
     * @param addressData to be geocoded
     * @return geocoded address
     */
    public Address geocode(AddressData addressData) {
        AddressBuilder addressBuilder = new AddressBuilder(addressData);
        if (dataIsIncomplete(addressData)) {
            return addressBuilder.build();
        }
        Optional<Address> addressOptional = service.readAddress(addressBuilder.build());
        if (addressOptional.isPresent()) {
            return addressOptional.get();
        }
        // Profinit geocoder for Czech places
        if (isCzechAddress(addressData)) {
            return profinitGeocoder.geocode(addressData);
        }
        // Google geocoder for foreign countries
        return googleGeocoder.geocode(addressData);
    }

    private boolean dataIsIncomplete(AddressData addressData) {
        boolean emptyCountryInfo = addressData.country() == null && addressData.countryCode() == null;
        return emptyCountryInfo || addressData.buildingNumber() == null ||
                addressData.city() == null || addressData.street() == null ||
                addressData.postalCode() == null;
    }

    private boolean isCzechAddress(AddressData addressData) {
        String czechCountryCode = "cz";
        String czechFullName = "česká republika";
        String countryLowerCase = addressData.country() != null ? addressData.country().toLowerCase() : null;
        String contryCodeLowerCase = addressData.countryCode() != null ? addressData.countryCode().toLowerCase() : null;
        return Objects.equals(contryCodeLowerCase, czechCountryCode) ||
                Objects.equals(countryLowerCase, czechFullName);
    }
}
