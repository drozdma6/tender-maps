package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.CompanyJpaRepository;
import cz.cvut.fit.bap.parser.domain.Company;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class handling communication with company repository
 */
@Service
public class CompanyService extends AbstractCreateService<Company, Long>{
    public CompanyService(CompanyJpaRepository repository){
        super(repository);
    }

    /**
     * Reads company from database by name
     *
     * @param name of wanted company
     * @return optional of company
     */
    public Optional<Company> readByName(String name){
        return ((CompanyJpaRepository) repository).findCompanyByName(name);
    }
}
