package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.domain.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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

    /**
     * Reads by ID
     *
     * @param id row id
     * @return optional of entity
     */
    public Optional<E> readById(K id) {
        return repository.findById(id);
    }
}

