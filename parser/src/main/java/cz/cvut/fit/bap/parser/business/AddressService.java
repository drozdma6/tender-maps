package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.AddressJpaRepository;
import cz.cvut.fit.bap.parser.domain.Address;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService extends AbstractCreateService<Address, Long>{
    protected AddressService(AddressJpaRepository addressJpaRepository){
        super(addressJpaRepository);
    }

    @Override
    public Address create(Address entity){
        Optional<Address> addressOptional = readAddress(entity);
        return addressOptional.orElseGet(() -> super.create(entity));
    }

    public Optional<Address> readAddress(Address address){
        return ((AddressJpaRepository) repository).readAddress(address);
    }
}
