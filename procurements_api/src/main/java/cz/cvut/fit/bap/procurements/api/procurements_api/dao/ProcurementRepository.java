package cz.cvut.fit.bap.procurements.api.procurements_api.dao;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Class representing procurement jpa repository
 */
@Repository
public interface ProcurementRepository extends JpaRepository<Procurement, Long> {

    /**
     * Gets all procurements where latitude and longitude is not null
     *
     * @return collection of procurements with exact address
     */
    @Query("SELECT p FROM Procurement p " +
            "JOIN FETCH p.supplier s " +
            "JOIN FETCH s.address " +
            "WHERE s.address.latitude IS NOT NULL " +
            "AND s.address.longitude IS NOT NULL ")
    Collection<Procurement> getProcurementsWithNonNullSupplierAddress();

    /**
     * Gets all procurements where latitude and longitude is not null and place of performance is in a list.
     *
     * @param placesOfPerformance list of places of performance
     * @return collection of procurements with exact address and match filtering
     */
    @Query("SELECT p FROM Procurement p " +
            "JOIN FETCH p.supplier s " +
            "JOIN FETCH s.address " +
            "WHERE s.address.latitude IS NOT NULL " +
            "AND s.address.longitude IS NOT NULL " +
            "AND p.placeOfPerformance IN :placesOfPerformance")
    Collection<Procurement> getProcurementsWithNonNullSupplierAddressPlace(@Param("placesOfPerformance") List<String> placesOfPerformance);


    /**
     * Gets all procurements where latitude and longitude is not null and authority is in a list.
     *
     * @param authoritiesIDs list of wanted contractor authorities
     * @return collection of procurements with exact address and match filtering
     */
    @Query("SELECT p FROM Procurement p " +
            "JOIN FETCH p.supplier s " +
            "JOIN FETCH s.address " +
            "WHERE s.address.latitude IS NOT NULL " +
            "AND s.address.longitude IS NOT NULL " +
            "AND p.contractorAuthority.id IN :authoritiesIDs")
    Collection<Procurement> getProcurementsWithNonNullSupplierAddressContractor(@Param("authoritiesIDs") List<Long> authoritiesIDs);

    /**
     * Gets all procurements where latitude and longitude is not null and authority and place of performance is in lists.
     *
     * @param authoritiesIDs      list of wanted contractor authorities
     * @param placesOfPerformance list of wanted places of performance
     * @return collection of procurements with exact address and match filtering
     */
    @Query("SELECT p FROM Procurement p " +
            "JOIN FETCH p.supplier s " +
            "JOIN FETCH s.address " +
            "WHERE s.address.latitude IS NOT NULL " +
            "AND s.address.longitude IS NOT NULL " +
            "AND p.placeOfPerformance IN :placesOfPerformance " +
            "AND p.contractorAuthority.id IN :authoritiesIDs")
    Collection<Procurement> getProcurementsWithNonNullSupplierAddress(@Param("placesOfPerformance") List<String> placesOfPerformance,
                                                                      @Param("authoritiesIDs") List<Long> authoritiesIDs);

    /**
     * Gets all procurements with given supplierId
     *
     * @param supplierId of company
     * @return procurements supplied by wanted supplierId
     */
    Collection<Procurement> findBySupplierId(Long supplierId);
}
