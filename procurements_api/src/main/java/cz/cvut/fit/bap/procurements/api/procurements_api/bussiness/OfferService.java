package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.OfferRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

/*
    Class handling business logic with offers
 */
@Service
public class OfferService extends AbstractService<Offer, Long>{
    protected OfferService(JpaRepository<Offer,Long> repository){
        super(repository);
    }

    /**
     * Gets all offers created by companyId.
     *
     * @param companyId id of company
     * @return all offers created by companyId
     */
    public Collection<Offer> getOffersByCompanyId(Long companyId){
        return ((OfferRepository) repository).findByCompany_Id(companyId);
    }
}
