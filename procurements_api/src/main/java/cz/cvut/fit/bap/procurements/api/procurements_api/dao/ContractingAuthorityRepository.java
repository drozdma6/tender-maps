package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractingAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Class representing contracting authority jpa repository
 */
@Repository
public interface ContractingAuthorityRepository extends JpaRepository<ContractingAuthority, Long> {
}
