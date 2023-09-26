package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.OfferRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import cz.cvut.fit.bap.procurements.api.procurements_api.utilities.specifications.OfferSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/*
    Class handling business logic with offers
 */
@Service
public class OfferService extends AbstractService<Offer, Long> {
    protected OfferService(JpaRepository<Offer, Long> repository) {
        super(repository);
    }

    /**
     * Gets all offers created by provided companyID matching desired filtering.
     *
     * @param companyId               of searched company
     * @param placesOfPerformance     filtering by places of performance
     * @param contractingAuthorityIds filtering by contracting authority ids
     * @return offers created by company matching filtering
     */
    public Collection<Offer> getOffersByCompanyId(Long companyId, List<String> placesOfPerformance, List<Long> contractingAuthorityIds) {
        return ((OfferRepository) repository).findAll(
                OfferSpecification.getCompanyOffersSpecification(placesOfPerformance, contractingAuthorityIds, companyId));
    }
}
