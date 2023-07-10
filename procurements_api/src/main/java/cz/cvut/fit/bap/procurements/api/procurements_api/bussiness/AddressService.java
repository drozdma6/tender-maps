package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends AbstractService<Address, Long>{
    protected AddressService(JpaRepository<Address,Long> repository){
        super(repository);
    }
}
