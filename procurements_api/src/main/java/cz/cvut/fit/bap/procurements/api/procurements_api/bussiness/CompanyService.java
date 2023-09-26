package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.dao.CompanyRepository;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import cz.cvut.fit.bap.procurements.api.procurements_api.utilities.specifications.CompanySpecification;
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
     * Gets all companies matching filtering
     *
     * @param placesOfPerformance     of supplied procurements or of procurements for which company made offers for
     * @param contractingAuthorityIds of supplied procurements or of procurements for which company made offers for
     * @param hasExactAddress         if company has exact geolocation
     * @param isSupplier              if company has already supplied some procurements
     * @return suppliers which match filtering
     */
    public Collection<Company> readAll(List<String> placesOfPerformance,
                                       List<Long> contractingAuthorityIds,
                                       Boolean hasExactAddress,
                                       Boolean isSupplier) {
        return ((CompanyRepository) repository).findAll(CompanySpecification.getCompanies(
                placesOfPerformance,
                contractingAuthorityIds,
                hasExactAddress,
                isSupplier
        ));
    }
}
