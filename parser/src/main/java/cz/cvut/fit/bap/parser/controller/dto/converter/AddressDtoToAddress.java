package cz.cvut.fit.bap.parser.controller.dto.converter;

import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/*
    Converts addressDto to address
 */
@Component
public class AddressDtoToAddress implements Function<AddressDto, Address>{
    @Override
    public Address apply(AddressDto addressDto){
        Address address = new Address();
        address.setCity(addressDto.city());
        address.setPostalCode(addressDto.postalCode());
        address.setStreet(addressDto.street());
        address.setBuildingNumber(addressDto.buildingNumber());
        if(addressDto.getCountryCode() != null){
            address.setCountryCode(addressDto.getCountryCode());
        }
        return address;
    }
}
