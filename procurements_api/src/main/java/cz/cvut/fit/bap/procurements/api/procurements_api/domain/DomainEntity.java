package cz.cvut.fit.bap.procurements.api.procurements_api.domain;

import java.io.Serializable;

/**
 * Common supertype for domain types.
 *
 * @param <ID> primary key type
 */
public interface DomainEntity<ID> extends Serializable{
    /**
     * @return the primary key value of this instance
     */
    ID getId();

    void setId(ID id);
}
