package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Class representing procurement jpa repository
 */
@Repository
public interface ProcurementRepository extends JpaRepository<Procurement, Long>, JpaSpecificationExecutor<Procurement> {
    /**
     * Gets all procurements with given supplierId
     *
     * @param supplierId of company
     * @return procurements supplied by wanted supplierId
     */
    Collection<Procurement> findBySupplierId(Long supplierId);
}
