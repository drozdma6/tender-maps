package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.CompanyRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import cz.cvut.fit.bap.procurements.api.procurements_api.specifications.NonSupplierSpecification;
import cz.cvut.fit.bap.procurements.api.procurements_api.specifications.SupplierSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/*
    Class handling business logic with companies
 */
@Service
public class CompanyService extends AbstractService<Company, Long> {
    protected CompanyService(JpaRepository<Company, Long> repository) {
        super(repository);
    }

    /**
     * Gets all companies which are suppliers of at least one procurement and match filtering.
     *
     * @return all suppliers.
     */
    public Collection<Company> getSuppliers(List<String> placesOfPerformance,
                                            List<Long> contractorAuthorityIds) {
        return ((CompanyRepository) repository).findAll(new SupplierSpecification(placesOfPerformance, contractorAuthorityIds));
    }

    /**
     * Gets all companies which have not yet won any procurements and match filtering.
     *
     * @return companies which are not suppliers
     */
    public Collection<Company> getNonSuppliers(List<String> placesOfPerformance,
                                               List<Long> contractorAuthorityIds) {
        return ((CompanyRepository) repository).findAll(new NonSupplierSpecification(placesOfPerformance, contractorAuthorityIds));
    }
}
