package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GeoLocationJpaRepository extends JpaRepository<GeoLocation, Long>{
}
