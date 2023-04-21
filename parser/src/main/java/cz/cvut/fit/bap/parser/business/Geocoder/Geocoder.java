package cz.cvut.fit.bap.parser.business.Geocoder;

import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;

public interface Geocoder{
    Address geocode(AddressDto addressDto);
}