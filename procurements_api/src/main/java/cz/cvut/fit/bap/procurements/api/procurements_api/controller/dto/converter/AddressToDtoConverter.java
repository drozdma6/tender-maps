package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.AddressDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AddressToDtoConverter implements Function<Address,AddressDto>{
    @Override
    public AddressDto apply(Address address){
        return new AddressDto(address.getId(), address.getBuildingNumber(), address.getCity(), address.getStreet(),
                address.getCountryCode(), address.getPostalCode(), address.getLatitude(), address.getLongitude());
    }
}
