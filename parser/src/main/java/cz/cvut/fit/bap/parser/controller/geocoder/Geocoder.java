package cz.cvut.fit.bap.parser.controller.geocoder;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.domain.Address;

public interface Geocoder{
    Address geocode(AddressData addressData);
}