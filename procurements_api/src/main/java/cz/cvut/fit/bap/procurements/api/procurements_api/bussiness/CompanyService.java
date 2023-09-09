package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.CompanyRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import cz.cvut.fit.bap.procurements.api.procurements_api.specifications.CompanySpecification;
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
     * Gets all companies which are suppliers for at least one procurement and match filtering
     *
     * @param placesOfPerformance    of supplied procurements or of procurements for which company made offers for
     * @param contractorAuthorityIds of supplied procurements or of procurements for which company made offers for
     * @param hasExactAddress        if company has exact geolocation
     * @return suppliers which match filtering
     */
    public Collection<Company> getSuppliers(List<String> placesOfPerformance,
                                            List<Long> contractorAuthorityIds,
                                            Boolean hasExactAddress) {
        return ((CompanyRepository) repository).findAll(CompanySpecification.getSuppliers(
                placesOfPerformance,
                contractorAuthorityIds,
                hasExactAddress
        ));
    }

    /**
     * Gets all companies which are not yet suppliers and match filtering
     *
     * @param placesOfPerformance    of procurements for which company made offers for
     * @param contractorAuthorityIds of procurements for which company made offers for
     * @param hasExactAddress        if company has exact geolocation
     * @return non-suppliers which match filtering
     */
    public Collection<Company> getNonSuppliers(List<String> placesOfPerformance,
                                               List<Long> contractorAuthorityIds,
                                               Boolean hasExactAddress) {
        return ((CompanyRepository) repository).findAll(CompanySpecification.getNonSuppliers(
                placesOfPerformance,
                contractorAuthorityIds,
                hasExactAddress
        ));
    }
}
