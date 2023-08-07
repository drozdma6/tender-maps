package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.AddressDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Domain entity to dto converter
 */
@Component
public class AddressToDtoConverter implements Function<Address, AddressDto> {
    /**
     * Converts address to dto
     *
     * @param address which is supposed to be converted
     * @return address dto
     */
    @Override
    public AddressDto apply(Address address) {
        return new AddressDto(address.getId(), address.getBuildingNumber(), address.getCity(), address.getStreet(),
                address.getCountryCode(), address.getPostalCode(), address.getLatitude(), address.getLongitude());
    }
}
