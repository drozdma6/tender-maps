package cz.cvut.fit.bap.procurements.api.procurements_api.specifications;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Abstract filter specification
 *
 * @param <T> domain type for specification
 */
public abstract class AbstractFilterSpecification<T> implements Specification<T> {
    private final List<String> placesOfPerformance;
    private final List<Long> contractingAuthorityIds;

    protected AbstractFilterSpecification(List<String> placesOfPerformance, List<Long> contractingAuthorityIds) {
        this.placesOfPerformance = placesOfPerformance;
        this.contractingAuthorityIds = contractingAuthorityIds;
    }

    public List<String> getPlacesOfPerformance() {
        return placesOfPerformance;
    }

    public List<Long> getContractingAuthorityIds() {
        return contractingAuthorityIds;
    }
}
