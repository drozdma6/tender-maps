package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.ProcurementRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ProcurementService extends AbstractService<Procurement, Long>{
    protected ProcurementService(JpaRepository<Procurement,Long> repository){
        super(repository);
    }

    /**
     * Gets all procurements which supplier has exact address.
     *
     * @return procurements with exact supplier's address
     */
    public Collection<Procurement> getProcurementsWithExactAddress() {
        return ((ProcurementRepository) repository).getProcurementsWithNonNullSupplierAddress();
    }
}
