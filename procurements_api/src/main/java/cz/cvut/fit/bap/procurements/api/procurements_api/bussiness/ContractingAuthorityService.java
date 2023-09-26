package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractingAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/*
    Class handling business logic with contracting authorities
 */
@Service
public class ContractingAuthorityService extends AbstractService<ContractingAuthority, Long> {
    protected ContractingAuthorityService(JpaRepository<ContractingAuthority, Long> repository) {
        super(repository);
    }
}
