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

    public Optional<ContractorAuthority> readByName(String name){
        return ((ContractorAuthorityJpaRepository) repository).findContractorAuthorityByName(name);
    }
}
