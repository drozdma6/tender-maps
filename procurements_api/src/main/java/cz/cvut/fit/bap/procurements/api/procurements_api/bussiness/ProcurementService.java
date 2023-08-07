package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.ProcurementRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
     * @param contractorAuthorityIds filtering by contracotorAuthorityIds
     * @return All procurements satisfying filtering.
     */
    public Collection<Procurement> getProcurementsWithExactAddress(Optional<List<String>> placesOfPerformance, Optional<List<Long>> contractorAuthorityIds) {
        if (placesOfPerformance.isEmpty() && contractorAuthorityIds.isEmpty()) {
            return ((ProcurementRepository) repository).getProcurementsWithNonNullSupplierAddress();
        } else if (placesOfPerformance.isEmpty()) {
            return ((ProcurementRepository) repository).getProcurementsWithNonNullSupplierAddressContractor(contractorAuthorityIds.get());
        } else if (contractorAuthorityIds.isEmpty()) {
            return ((ProcurementRepository) repository).getProcurementsWithNonNullSupplierAddressPlace(placesOfPerformance.get());
        }
        return ((ProcurementRepository) repository).getProcurementsWithNonNullSupplierAddress(placesOfPerformance.get(), contractorAuthorityIds.get());
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
