package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContractorAuthorityJpaRepository;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class handling communication with contractor authority repository
 */
@Service
public class ContractorAuthorityService extends AbstractCreateService<ContractorAuthority, Long>{
    public ContractorAuthorityService(ContractorAuthorityJpaRepository repository){
        super(repository);
    }

    /**
     * Reads contractor authority by name
     *
     * @param name of wanted contractor authority
     * @return optional of contractor authority
     */
    public Optional<ContractorAuthority> readByName(String name){
        return ((ContractorAuthorityJpaRepository) repository).findContractorAuthorityByName(name);
    }
}
