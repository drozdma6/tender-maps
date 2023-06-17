package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AbstractCreateService;
import cz.cvut.fit.bap.parser.domain.DomainEntity;

/**
 * Abstract controller.
 *
 * @param <S> the type of service
 * @param <E> the type of entity
 * @param <K> the type of entity key
 */
public abstract class AbstractController<S extends AbstractCreateService<E,K>, E extends DomainEntity<K>, K>{
    protected final S service;

    protected AbstractController(S service){
        this.service = service;
    }

    /**
     * Saves an entity through the service.
     *
     * @param entity the entity to be stored
     * @return the stored entity
     */
    public E save(E entity){
        return service.create(entity);
    }
}