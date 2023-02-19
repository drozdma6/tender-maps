package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.domain.DomainEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractCreateService<E extends DomainEntity<K>, K>{
    protected final JpaRepository<E, K> repository;

    protected AbstractCreateService(JpaRepository<E, K> repository){
        this.repository = repository;
    }

    @Transactional
    public E create(E entity){
        K id = entity.getId();
        if (id != null && repository.existsById(id))
            throw new RuntimeException();
        return repository.save(entity);
    }
}

