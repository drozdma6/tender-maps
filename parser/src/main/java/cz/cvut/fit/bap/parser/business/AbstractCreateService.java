package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.domain.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Abstract class handling creating entities through dao layer
 *
 * @param <E> domain entity
 * @param <K> key of domain entity
 */
public abstract class AbstractCreateService<E extends DomainEntity<K>, K>{
    protected final JpaRepository<E, K> repository;

    protected AbstractCreateService(JpaRepository<E, K> repository){
        this.repository = repository;
    }

    /**
     * Stores entity in database
     *
     * @param entity which is supposed to be stored
     * @return stored entity
     */
    public E create(E entity){
        return repository.save(entity);
    }
}

