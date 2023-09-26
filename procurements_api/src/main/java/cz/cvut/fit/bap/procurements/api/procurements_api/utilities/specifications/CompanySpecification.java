package cz.cvut.fit.bap.procurements.api.procurements_api.utilities.specifications;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

/*
    Utility class for company specifications.
 */
public class CompanySpecification {
    private CompanySpecification() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets companies specification with filtering.
     *
     * @param placesOfPerformance     filtering by places of performance
     * @param contractingAuthorityIds filtering by contracting authorities
     * @param hasExactAddress         if latitude/longitude is not null
     * @param isSupplier              filters only companies which are supplier for at least one procurement
     * @return company specification matching desired filtering
     */
    public static Specification<Company> getCompanies(List<String> placesOfPerformance, List<Long> contractingAuthorityIds, Boolean hasExactAddress, boolean isSupplier) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Predicate supplierStatusPredicate = isSupplier
                    ? criteriaBuilder.isNotEmpty(root.get("suppliedProcurements"))  // is supplier
                    : criteriaBuilder.isEmpty(root.get("suppliedProcurements"));    // is not supplier
            return criteriaBuilder.and(supplierStatusPredicate,
                    buildPlaceOfPerformancePredicate(root, criteriaBuilder, placesOfPerformance),
                    buildGeoLocationPredicate(root, criteriaBuilder, hasExactAddress),
                    buildContractingAuthorityPredicate(root, criteriaBuilder, contractingAuthorityIds));
        };
    }

    private static Predicate buildGeoLocationPredicate(Root<Company> root, CriteriaBuilder criteriaBuilder, Boolean hasExactAddress) {
        if (!Objects.nonNull(hasExactAddress)) {
            return criteriaBuilder.conjunction();
        }
        Path<Double> lat = root.get("address").get("latitude");
        Path<Double> lng = root.get("address").get("longitude");
        return hasExactAddress ?
                criteriaBuilder.and(criteriaBuilder.isNotNull(lat), criteriaBuilder.isNotNull(lng)) :
                criteriaBuilder.and(criteriaBuilder.isNull(lat), criteriaBuilder.isNull(lng));
    }

    private static Predicate buildPlaceOfPerformancePredicate(Root<Company> root, CriteriaBuilder criteriaBuilder, List<String> placesOfPerformance) {
        if (placesOfPerformance == null || placesOfPerformance.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        return root.get("offers").get("procurement").get("placeOfPerformance").in(placesOfPerformance);
    }

    private static Predicate buildContractingAuthorityPredicate(Root<Company> root, CriteriaBuilder criteriaBuilder, List<Long> contractingAuthorityIds) {
        if (contractingAuthorityIds == null || contractingAuthorityIds.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        return root.get("offers").get("procurement").get("contractingAuthority").get("id").in(contractingAuthorityIds);
    }
}
