package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.AddressJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class for handling communication with address repository
 */
@Service
public class AddressService extends AbstractCreateService<Address, Long>{
    public AddressService(AddressJpaRepository addressJpaRepository){
        super(addressJpaRepository);
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

