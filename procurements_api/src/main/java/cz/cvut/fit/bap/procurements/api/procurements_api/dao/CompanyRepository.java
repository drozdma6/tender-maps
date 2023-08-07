package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Collection;

/**
 * Class representing company jpa repository
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    /**
     * Gets all companies which are already suppliers for some procurements
     *
     * @return suppliers
     */
    @Query(nativeQuery = true, value =
            "SELECT DISTINCT c.* FROM Company c JOIN Procurement p ON p.supplier_id = c.id")
    Collection<Company> findSuppliers();

    /**
     * Gets all companies which are not yet suppliers for any procurements
     *
     * @return all non-suppliers
     */
    @Query(nativeQuery = true, value = "SELECT DISTINCT c.* " +
            "FROM Company c " +
            "LEFT JOIN Procurement p ON p.supplier_id = c.id " +
            "WHERE p.id IS NULL")
    Collection<Company> findNonSuppliers();
}
