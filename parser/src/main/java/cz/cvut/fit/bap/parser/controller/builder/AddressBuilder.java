package cz.cvut.fit.bap.parser.controller.builder;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.domain.Address;

/*
    Builder class for address
 */
public class AddressBuilder implements Builder<Long, Address> {
    private final String buildingNumber;
    private final String city;
    private final String street;
    private final String postalCode;
    private final String landRegistryNumber;
    private String countryCode;
    private Double latitude;
    private Double longitude;

    public AddressBuilder(AddressData addressData) {
        this.buildingNumber = addressData.buildingNumber();
        this.city = addressData.city();
        this.street = addressData.street();
        this.countryCode = addressData.countryCode();
        this.postalCode = addressData.postalCode();
        this.landRegistryNumber = addressData.landRegistryNumber();
    }

    public AddressBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public AddressBuilder latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public AddressBuilder longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Address build() {
        return new Address(countryCode,
                city,
                postalCode,
                street,
                buildingNumber,
                landRegistryNumber,
                latitude,
                longitude
        );
    }
}