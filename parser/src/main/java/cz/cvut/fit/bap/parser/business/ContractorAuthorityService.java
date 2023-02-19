package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContractorAuthorityJpaRepository;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContractorAuthorityService extends AbstractCreateService<ContractorAuthority, Long>{
    public ContractorAuthorityService(ContractorAuthorityJpaRepository repository){
        super(repository);
    }

    @Override
    public ContractorAuthority create(ContractorAuthority entity){
        Optional<ContractorAuthority> contractorAuthority = ((ContractorAuthorityJpaRepository) repository).findContractorAuthorityByName(
                entity.getName());
        return contractorAuthority.orElseGet(() -> super.create(entity));
    }
}
