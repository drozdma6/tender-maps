package cz.cvut.fit.bap.procurements.api.procurements_api.utilities.specifications;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class OfferSpecification {
    private OfferSpecification() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Offer> getCompanyOffersSpecification(List<String> placesOfPerformance, List<Long> contractingAuthorityIds, Long companyId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("company").get("id"), companyId),
                buildPlaceOfPerformancePredicate(root, criteriaBuilder, placesOfPerformance),
                buildContractingAuthorityPredicate(root, criteriaBuilder, contractingAuthorityIds)
        );
    }

    private static Predicate buildPlaceOfPerformancePredicate(Root<Offer> root, CriteriaBuilder criteriaBuilder, List<String> placesOfPerformance) {
        return placesOfPerformance != null && !placesOfPerformance.isEmpty() ?
                root.get("procurement").get("placeOfPerformance").in(placesOfPerformance) :
                criteriaBuilder.conjunction();
    }

    private static Predicate buildContractingAuthorityPredicate(Root<Offer> root, CriteriaBuilder criteriaBuilder, List<Long> contractingAuthorityIds) {
        return contractingAuthorityIds != null && !contractingAuthorityIds.isEmpty() ?
                root.get("procurement").get("contractingAuthority").get("id").in(contractingAuthorityIds) :
                criteriaBuilder.conjunction();
    }
}
