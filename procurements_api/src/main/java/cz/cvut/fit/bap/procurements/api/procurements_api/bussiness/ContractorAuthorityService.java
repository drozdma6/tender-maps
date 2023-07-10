package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractorAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ContractorAuthorityService extends AbstractService<ContractorAuthority, Long>{
    protected ContractorAuthorityService(JpaRepository<ContractorAuthority,Long> repository){
        super(repository);
    }
}
