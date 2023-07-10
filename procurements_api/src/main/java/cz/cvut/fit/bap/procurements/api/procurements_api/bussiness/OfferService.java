package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class OfferService extends AbstractService<Offer, Long>{
    protected OfferService(JpaRepository<Offer,Long> repository){
        super(repository);
    }
}
