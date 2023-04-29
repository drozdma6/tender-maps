package cz.cvut.fit.bap.parser.controller.Geocoder;

import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.domain.Address;

public interface Geocoder{
    Address geocode(AddressDto addressDto);
}