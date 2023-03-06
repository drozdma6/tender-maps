package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ProcurementJpaRepository;
import cz.cvut.fit.bap.parser.domain.Procurement;
import org.springframework.stereotype.Service;

@Service
public class ProcurementService extends AbstractCreateService<Procurement, Long>{
    public ProcurementService(ProcurementJpaRepository repository){
        super(repository);
    }

    public boolean existsBySystemNumber(String systemNumber){
        return ((ProcurementJpaRepository) repository).existsProcurementBySystemNumber(
                systemNumber);
    }
}
