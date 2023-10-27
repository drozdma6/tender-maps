package cz.cvut.fit.bap.parser.controller.data.converter;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/*
    Converts addressData to address
 */
@Component
public class AddressDataToAddress implements Function<AddressData, Address>{
    @Override
    public Address apply(AddressData addressData){
        Address address = new Address();
        address.setCity(addressData.city());
        address.setPostalCode(addressData.postalCode());
        address.setStreet(addressData.street());
        address.setBuildingNumber(addressData.buildingNumber());
        if(addressData.getCountryCode() != null){
            address.setCountryCode(addressData.getCountryCode());
        }
        return address;
    }
}
