package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.domain.DomainEntity;
import jakarta.transaction.Transactional;
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
    @Transactional
    public E create(E entity){
        K id = entity.getId();
        if (id != null && repository.existsById(id))
            throw new RuntimeException();
        return repository.save(entity);
    }
}

