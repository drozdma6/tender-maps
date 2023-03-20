package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.OfferJpaRepository;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.OfferId;
import org.springframework.stereotype.Service;

/**
 * Class handling communication with offer repository
 */
@Service
public class OfferService extends AbstractCreateService<Offer, OfferId>{
    protected OfferService(OfferJpaRepository repository){
        super(repository);
    }
}
