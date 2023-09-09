package cz.cvut.fit.bap.procurements.api.procurements_api.specifications;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

/*
    Utility class for procurement specifications.
 */
public class ProcurementSpecification {
    private ProcurementSpecification() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets procurement specification with implementation of filtering.
     *
     * @param placesOfPerformance filtering by places of performance
     * @param contractingAuthorityIds filtering by contracting authorities
     * @param supplierHasExactAddress supplier has to have non null latitude and longitude
     * @param supplierId id of procurement's supplier
     * @return specification matching desired filtering
     */
    public static Specification<Procurement> getProcurementSpecification(List<String> placesOfPerformance,
                                                                         List<Long> contractingAuthorityIds,
                                                                         Boolean supplierHasExactAddress,
                                                                         Long supplierId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                buildGeoLocationPredicate(root, criteriaBuilder, supplierHasExactAddress),
                buildPlaceOfPerformancePredicate(root, criteriaBuilder, placesOfPerformance),
                buildContractingAuthorityPredicate(root, criteriaBuilder, contractingAuthorityIds),
                buildSupplierIdPredicate(root, criteriaBuilder, supplierId));
    }

    private static Predicate buildGeoLocationPredicate(Root<Procurement> root, CriteriaBuilder criteriaBuilder, Boolean supplierHasExactAddress) {
        if (Objects.nonNull(supplierHasExactAddress)) {
            Path<Double> supplierLatitude = root.get("supplier").get("address").get("latitude");
            Path<Double> supplierLongitude = root.get("supplier").get("address").get("longitude");
            return supplierHasExactAddress ?
                    criteriaBuilder.and(
                            criteriaBuilder.isNotNull(supplierLatitude),
                            criteriaBuilder.isNotNull(supplierLongitude)
                    ) :
                    criteriaBuilder.and(
                            criteriaBuilder.isNull(supplierLatitude),
                            criteriaBuilder.isNull(supplierLongitude)
                    );
        }
        return criteriaBuilder.conjunction();
    }

    private static Predicate buildPlaceOfPerformancePredicate(Root<Procurement> root, CriteriaBuilder criteriaBuilder, List<String> placesOfPerformance) {
        return placesOfPerformance != null && !placesOfPerformance.isEmpty() ?
                root.get("placeOfPerformance").in(placesOfPerformance) :
                criteriaBuilder.conjunction();
    }

    private static Predicate buildContractingAuthorityPredicate(Root<Procurement> root, CriteriaBuilder criteriaBuilder, List<Long> contractingAuthorityIds) {
        return contractingAuthorityIds != null && !contractingAuthorityIds.isEmpty() ?
                root.get("contractorAuthority").get("id").in(contractingAuthorityIds) :
                criteriaBuilder.conjunction();
    }

    private static Predicate buildSupplierIdPredicate(Root<Procurement> root, CriteriaBuilder criteriaBuilder, Long supplierId) {
        return supplierId != null ?
                criteriaBuilder.equal(root.get("supplier").get("id"), supplierId) :
                criteriaBuilder.conjunction();
    }
}
