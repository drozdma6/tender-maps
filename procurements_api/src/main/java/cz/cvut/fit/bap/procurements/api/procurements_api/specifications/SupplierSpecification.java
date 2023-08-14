package cz.cvut.fit.bap.procurements.api.procurements_api.specifications;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * Specification to get all companies which have already won some procurements while fulfilling desired filtering
 */
public class SupplierSpecification extends AbstractFilterSpecification<Company> {

    public SupplierSpecification(List<String> placesOfPerformance, List<Long> contractingAuthorityIds) {
        super(placesOfPerformance, contractingAuthorityIds);
    }

    /**
     * Get predicate for companies which are suppliers and their geo-location is not null. That supplies or participate
     * in procurements which place of performance is in getPlacesOfPerformance() and contractor authority is
     * in getContractingAuthorityIds().
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
                criteriaBuilder.isNotEmpty(root.get("suppliedProcurements"))
        );

        if (getPlacesOfPerformance() != null && !getPlacesOfPerformance().isEmpty()) {
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.or(
                            root.get("suppliedProcurements").get("placeOfPerformance").in(getPlacesOfPerformance()),
                            root.get("offers").get("procurement").get("placeOfPerformance").in(getPlacesOfPerformance())
                    )

            );
        }

        if (getContractingAuthorityIds() != null && !getContractingAuthorityIds().isEmpty()) {
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.or(
                            root.get("suppliedProcurements").get("contractorAuthority").get("id").in(getContractingAuthorityIds()),
                            root.get("offers").get("procurement").get("contractorAuthority").get("id").in(getContractingAuthorityIds())
                    )

            );
        }

        return predicate;
    }
}
