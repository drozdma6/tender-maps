package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.ProcurementRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import cz.cvut.fit.bap.procurements.api.procurements_api.utilities.specifications.ProcurementSpecification;
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
     * Gets all procurements matching filtering by parameters.
     *
     * @param placesOfPerformance     filtering by place of performance
     * @param contractorAuthorityIds  filtering by contractor authorities Ids
     * @param supplierHasExactAddress if supplier has exact geolocation
     * @param supplierId              filtering by supplierId
     * @return All procurements satisfying filtering.
     */
    public Collection<Procurement> readAll(List<String> placesOfPerformance,
                                           List<Long> contractorAuthorityIds,
                                           Boolean supplierHasExactAddress,
                                           Long supplierId) {
        return ((ProcurementRepository) repository).findAll(ProcurementSpecification.getProcurementSpecification(
                placesOfPerformance,
                contractorAuthorityIds,
                supplierHasExactAddress,
                supplierId));
    }
}
