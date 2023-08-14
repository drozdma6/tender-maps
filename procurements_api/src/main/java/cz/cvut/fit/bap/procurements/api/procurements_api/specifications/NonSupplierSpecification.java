package cz.cvut.fit.bap.procurements.api.procurements_api.specifications;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import jakarta.persistence.criteria.*;

import java.util.List;

/**
 * Specification to get all companies which have not yet won any procurements while fulfilling desired filtering
 */
public class NonSupplierSpecification extends AbstractFilterSpecification<Company> {

    public NonSupplierSpecification(List<String> placesOfPerformance, List<Long> contractingAuthorityIds) {
        super(placesOfPerformance, contractingAuthorityIds);
    }

    /**
     * Get predicate for companies which are not suppliers and their geo-location is not null. That participate in procurements
     * which place of performance is in getPlacesOfPerformance() and contractor authority is in getContractingAuthorityIds().
     *
     * @param root            must not be {@literal null}.
     * @param query           must not be {@literal null}.
     * @param criteriaBuilder must not be {@literal null}.
     * @return predicate matching desired select
     */
    @Override
    public Predicate toPredicate(Root<Company> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("address").get("latitude")),
                criteriaBuilder.isNotNull(root.get("address").get("longitude")),
                criteriaBuilder.isEmpty(root.get("suppliedProcurements"))
        );

        if (getPlacesOfPerformance() != null && !getPlacesOfPerformance().isEmpty()) {
            predicate = criteriaBuilder.and(predicate,
                    root.get("offers").get("procurement").get("placeOfPerformance").in(getPlacesOfPerformance())
            );
        }

        if (getContractingAuthorityIds() != null && !getContractingAuthorityIds().isEmpty()) {
            predicate = criteriaBuilder.and(predicate,
                    root.get("offers").get("procurement").get("contractorAuthority").get("id").in(getContractingAuthorityIds())
            );
        }

        return predicate;
    }
}
