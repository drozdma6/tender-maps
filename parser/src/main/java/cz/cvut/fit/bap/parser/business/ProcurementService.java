package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ProcurementJpaRepository;
import cz.cvut.fit.bap.parser.domain.Procurement;
import org.springframework.stereotype.Service;

/**
 * Class handling communication with procurement repository
 */
@Service
public class ProcurementService extends AbstractCreateService<Procurement, Long>{
    public ProcurementService(ProcurementJpaRepository repository){
        super(repository);
    }

    /**
     * Finds if procurement with provided systemNumber already exists in database
     *
     * @param systemNumber of procurement
     * @return true if procurement exits, false if it doesnt
     */
    public boolean existsBySystemNumber(String systemNumber){
        return ((ProcurementJpaRepository) repository).existsProcurementBySystemNumber(
                systemNumber);
    }
}
