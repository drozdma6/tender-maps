package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Class representing procurement jpa repository
 */
@Repository
public interface ProcurementRepository extends JpaRepository<Procurement, Long>{

    /**
     * Gets all procurements where latitude and longitude is not null.
     *
     * @return collection of procurements with exact address
     */
    @Query("SELECT p FROM Procurement p " +
            "JOIN FETCH p.supplier s " +
            "JOIN FETCH s.address " +
            "WHERE s.address.latitude IS NOT NULL " +
            "AND s.address.longitude IS  NOT  NULL")
    Collection<Procurement> getProcurementsWithNonNullSupplierAddress();
}
