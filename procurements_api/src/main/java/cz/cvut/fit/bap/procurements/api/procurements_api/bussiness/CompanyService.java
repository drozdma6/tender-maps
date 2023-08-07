package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.CompanyRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.util.Collection;

/*
    Class handling business logic with companies
 */
@Service
public class CompanyService extends AbstractService<Company, Long> {
    protected CompanyService(JpaRepository<Company, Long> repository) {
        super(repository);
    }

    /**
     * Gets all companies which are suppliers of at least one procurement.
     *
     * @return all suppliers.
     */
    public Collection<Company> getSuppliers() {
        return ((CompanyRepository) repository).findSuppliers();
    }

    /**
     * Gets all companies which have not yet won any procurements.
     *
     * @return companies which are not suppliers
     */
    public Collection<Company> getCompaniesWithoutWonProcurements() {
        return ((CompanyRepository) repository).findNonSuppliers();
    }
}
