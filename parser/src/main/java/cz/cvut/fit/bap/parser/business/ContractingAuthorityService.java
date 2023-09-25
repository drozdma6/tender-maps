package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContractingAuthorityJpaRepository;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class handling communication with contracting authority repository
 */
@Service
public class ContractingAuthorityService extends AbstractCreateService<ContractingAuthority, Long> {
    public ContractingAuthorityService(ContractingAuthorityJpaRepository repository) {
        super(repository);
    }

    /**
     * Reads contracting authority by name
     *
     * @param name of wanted contracting authority
     * @return optional of contracting authority
     */
    public Optional<ContractingAuthority> readByName(String name) {
        return ((ContractingAuthorityJpaRepository) repository).findContractingAuthorityByName(name);
    }
}
