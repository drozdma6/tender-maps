package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.OfferJpaRepository;
import cz.cvut.fit.bap.parser.domain.Offer;
import org.springframework.stereotype.Service;

/**
 * Class handling communication with offer repository
 */
@Service
public class OfferService extends AbstractCreateService<Offer,Long>{
    public OfferService(OfferJpaRepository repository){
        super(repository);
    }
}
