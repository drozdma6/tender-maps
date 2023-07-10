package cz.cvut.fit.bap.procurements.api.procurements_api.bussiness;

import cz.cvut.fit.bap.procurements.api.procurements_api.domain.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * Common superclass for business logic of all entities.
 *
 * @param <E> Type of entity
 * @param <K> Type of (primary) key.
 */
public abstract class AbstractService<E extends DomainEntity<K>, K>{
    protected final JpaRepository<E,K> repository;

    protected AbstractService(JpaRepository<E,K> repository){
        this.repository = repository;
    }

    /**
     * Reads entity by ID.
     *
     * @param id of entity
     * @return optional of entity with given id
     */
    public Optional<E> readById(K id){
        return repository.findById(id);
    }

    /**
     * Reads all from table.
     *
     * @return collection of entities
     */
    public Collection<E> readAll(){
        return repository.findAll();
    }
}
