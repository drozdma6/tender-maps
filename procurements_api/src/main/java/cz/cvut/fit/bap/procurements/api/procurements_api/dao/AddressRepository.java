package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Class representing address jpa repository
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
}
