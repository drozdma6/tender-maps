package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.OfferId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferJpaRepository extends JpaRepository<Offer, OfferId>{
}
