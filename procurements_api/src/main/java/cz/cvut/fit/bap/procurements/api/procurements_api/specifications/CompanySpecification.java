package cz.cvut.fit.bap.procurements.api.procurements_api.specifications;

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
     * Gets supplier specification with filtering.
     *
     * @param placesOfPerformance filtering by places of performance
     * @param contractingAuthorityIds filtering by contracting authorities
     * @param hasExactAddress if latitude/longitude is not null
     * @return company specification matching desired filtering
     */
    public static Specification<Company> getSuppliers(List<String> placesOfPerformance, List<Long> contractingAuthorityIds, Boolean hasExactAddress) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Predicate supplierPredicate = criteriaBuilder.isNotEmpty(root.get("suppliedProcurements")); //is supplier
            return criteriaBuilder.and(supplierPredicate,
                    buildPlaceOfPerformancePredicate(root, criteriaBuilder, placesOfPerformance, true),
                    buildGeoLocationPredicate(root, criteriaBuilder, hasExactAddress),
                    buildContractingAuthorityPredicate(root, criteriaBuilder, contractingAuthorityIds, true));
        };
    }

    /**
     * Gets non-supplier specification with filtering.
     *
     * @param placesOfPerformance filtering by places of performance
     * @param contractingAuthorityIds filtering by contracting authorities
     * @param hasExactAddress if latitude/longitude is not null
     * @return company specification matching desired filtering
     */
    public static Specification<Company> getNonSuppliers(List<String> placesOfPerformance, List<Long> contractingAuthorityIds, Boolean hasExactAddress) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Predicate nonSupplierPredicate = criteriaBuilder.isEmpty(root.get("suppliedProcurements")); //is not supplier
            return criteriaBuilder.and(nonSupplierPredicate,
                    buildPlaceOfPerformancePredicate(root, criteriaBuilder, placesOfPerformance, false),
                    buildGeoLocationPredicate(root, criteriaBuilder, hasExactAddress),
                    buildContractingAuthorityPredicate(root, criteriaBuilder, contractingAuthorityIds, false));
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

    private static Predicate buildPlaceOfPerformancePredicate(Root<Company> root, CriteriaBuilder criteriaBuilder, List<String> placesOfPerformance, boolean forSupplier) {
        if (placesOfPerformance == null || placesOfPerformance.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        Predicate predicate = root.get("offers").get("procurement").get("placeOfPerformance").in(placesOfPerformance);
        if(forSupplier){
            return criteriaBuilder.or(predicate, root.get("suppliedProcurements").get("placeOfPerformance").in(placesOfPerformance));
        }
        return predicate;
    }

    private static Predicate buildContractingAuthorityPredicate(Root<Company> root, CriteriaBuilder criteriaBuilder, List<Long> contractingAuthorityIds, boolean forSupplier) {
        if (contractingAuthorityIds == null || contractingAuthorityIds.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        Predicate predicate = root.get("offers").get("procurement").get("contractorAuthority").get("id").in(contractingAuthorityIds);
        if(forSupplier){
            return criteriaBuilder.or(predicate, root.get("suppliedProcurements").get("contractorAuthority").get("id").in(contractingAuthorityIds));
        }
        return predicate;
    }
}
