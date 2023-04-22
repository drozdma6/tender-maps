package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.business.Geocoder.GoogleGeocodingApi;
import cz.cvut.fit.bap.parser.business.Geocoder.ProfinitGeocodingApi;
import cz.cvut.fit.bap.parser.dao.AddressJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import cz.cvut.fit.bap.parser.scrapper.dto.converter.AddressDtoToAddress;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Class for handling communication with address repository
 */
@Service
public class AddressService extends AbstractCreateService<Address, Long>{
    private final GoogleGeocodingApi googleGeocoder;
    private final ProfinitGeocodingApi profinitGeocoder;

    private final AddressDtoToAddress addressDtoToAddress;

    public AddressService(AddressJpaRepository addressJpaRepository,
                          GoogleGeocodingApi googleGeocoder, ProfinitGeocodingApi profinitGeocoder,
                          AddressDtoToAddress addressDtoToAddress){
        super(addressJpaRepository);
        this.googleGeocoder = googleGeocoder;
        this.profinitGeocoder = profinitGeocoder;
        this.addressDtoToAddress = addressDtoToAddress;
    }

    /*
        Forbids creating entity from Address
     */
    @Override
    public Address create(Address entity){
        throw new UnsupportedOperationException("Unsupported operation create in address");
    }

    /**
     * Creates new address entity from addressDto by setting country code, latitude and longitude
     *
     * @param addressDto which is supposed to be stored
     * @return address from database
     */
    public Address create(AddressDto addressDto){
        Optional<Address> addressOptional = readAddress(addressDto);
        if (addressOptional.isPresent()){
            return addressOptional.get();
        }
        if (dtoIsIncomplete(addressDto)){
            return super.create(addressDtoToAddress.apply(addressDto));
        }
        Address address;
        String country = addressDto.getCountry().toLowerCase();
        if (Objects.equals(country, "cz")){
            //profinit geocoder for czech places
            address = profinitGeocoder.geocode(addressDto);
        } else{
            address = googleGeocoder.geocode(addressDto);
        }
        return super.create(address);
    }

    /**
     * Reads address from database
     *
     * @param address which is supposed to be found
     * @return optional of address
     */
    public Optional<Address> readAddress(AddressDto address){
        return ((AddressJpaRepository) repository).readAddress(address);
    }

    private boolean dtoIsIncomplete(AddressDto addressDto){
        return addressDto.getCountry() == null || addressDto.getBuildingNumber() == null ||
               addressDto.getCity() == null || addressDto.getStreet() == null ||
               addressDto.getPostalCode() == null;
    }
}

