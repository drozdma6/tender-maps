package cz.cvut.fit.bap.parser.business;

import com.google.maps.model.GeocodingResult;
import cz.cvut.fit.bap.parser.dao.AddressJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class for handling communication with address repository
 */
@Service
public class AddressService extends AbstractCreateService<Address, Long>{
    private final GeocodingApiClient geocodingApiClient;

    protected AddressService(AddressJpaRepository addressJpaRepository,
                             GeocodingApiClient geocodingApiClient){
        super(addressJpaRepository);
        this.geocodingApiClient = geocodingApiClient;
    }

    /**
     * Creates new address entity, sets country state in short format, sets latitude and longitude
     *
     * @param entity which is supposed to be stored
     * @return entity which is supposed to be stored
     */
    @Override
    public Address create(Address entity){
        Optional<Address> addressOptional = readAddress(entity);
        if (addressOptional.isPresent()){
            return addressOptional.get();
        }
        GeocodingResult[] geocodingResult = geocodingApiClient.geocode(entity);
        entity.setLatitude(geocodingApiClient.getLat(geocodingResult));
        entity.setLongitude(geocodingApiClient.getLng(geocodingResult));
        entity.setCountryCode(geocodingApiClient.getCountryShortName(geocodingResult));
        return super.create(entity);
    }

    /**
     * Reads address from database
     *
     * @param address which is supposed to be found
     * @return optional of address
     */
    public Optional<Address> readAddress(Address address){
        return ((AddressJpaRepository) repository).readAddress(address);
    }
}

