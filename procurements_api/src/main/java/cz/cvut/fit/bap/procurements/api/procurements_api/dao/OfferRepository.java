package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Class representing offer jpa repository
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>{
    /**
     * Gets all offers by companyId
     *
     * @param companyId of company which created offer
     * @return offers created by company with companyId
     */
    Collection<Offer> findByCompany_Id(Long companyId);
}
