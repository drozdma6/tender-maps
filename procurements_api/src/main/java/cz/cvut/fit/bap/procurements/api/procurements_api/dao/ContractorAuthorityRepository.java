package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractorAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Class representing contractor authority jpa repository
 */
@Repository
public interface ContractorAuthorityRepository extends JpaRepository<ContractorAuthority, Long>{
}
