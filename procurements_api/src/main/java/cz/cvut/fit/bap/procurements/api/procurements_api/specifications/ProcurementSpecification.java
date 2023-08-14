package cz.cvut.fit.bap.procurements.api.procurements_api.specifications;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * Specification to get all procurements which suppliers have geo-location is not null and fulfilling desired filtering.
 */
public class ProcurementSpecification extends AbstractFilterSpecification<Procurement> {
    public ProcurementSpecification(List<String> placesOfPerformance, List<Long> authoritiesIDs) {
        super(placesOfPerformance, authoritiesIDs);
    }

    /**
     * Get predicate for procurements which suppliers have not null latitude and longitude and place of performance is in
     * getPlacesOfPerformance() and contractor authority is in getContractingAuthorityIds().
     *
     * @param root            must not be {@literal null}.
     * @param query           must not be {@literal null}.
     * @param criteriaBuilder must not be {@literal null}.
     * @return predicate matching desired select
     */
    @Override
    public Predicate toPredicate(Root<Procurement> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("supplier").get("address").get("latitude")),
                criteriaBuilder.isNotNull(root.get("supplier").get("address").get("longitude"))
        );

        if (getPlacesOfPerformance() != null && !getPlacesOfPerformance().isEmpty()) {
            predicate = criteriaBuilder.and(predicate,
                    root.get("placeOfPerformance").in(getPlacesOfPerformance())
            );
        }

        if (getContractingAuthorityIds() != null && !getContractingAuthorityIds().isEmpty()) {
            predicate = criteriaBuilder.and(predicate,
                    root.get("contractorAuthority").get("id").in(getContractingAuthorityIds())
            );
        }

        return predicate;
    }
}
