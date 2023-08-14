package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.ProcurementRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import cz.cvut.fit.bap.procurements.api.procurements_api.specifications.ProcurementSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/*
    Class handling business logic with procurements
 */
@Service
public class ProcurementService extends AbstractService<Procurement, Long> {
    protected ProcurementService(JpaRepository<Procurement, Long> repository) {
        super(repository);
    }

    /**
     * Gets all procurements which supplier has exact address and place of performance is in provided list
     * and contractor authority is in a list.
     *
     * @param placesOfPerformance    filtering by place of performance
     * @param contractorAuthorityIds filtering by contractor authorities Ids
     * @return All procurements satisfying filtering.
     */
    public Collection<Procurement> getProcurementsWithExactAddress(List<String> placesOfPerformance, List<Long> contractorAuthorityIds) {
        return ((ProcurementRepository) repository).findAll(new ProcurementSpecification(placesOfPerformance, contractorAuthorityIds));
    }

    /**
     * Gets all procurements supplied by company with provided id.
     *
     * @param supplierId of supplier
     * @return all procurements supplied by supplierId
     */
    public Collection<Procurement> getProcurementsBySupplierId(Long supplierId) {
        return ((ProcurementRepository) repository).findBySupplierId(supplierId);
    }
}
