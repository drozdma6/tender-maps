package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.DomainEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * Common superclass for api logic of all entities.
 *
 * @param <E> Type of entity
 * @param <K> Type of (primary) key.
 * @param <DTO> Type of dto object.
 */
public abstract class AbstractController<E extends DomainEntity<K>, K, DTO>{
    protected final AbstractService<E,K> service;

    protected final Function<E,DTO> toDtoConverter;

    protected AbstractController(AbstractService<E,K> service,
                                 Function<E,DTO> toDtoConverter){
        this.service = service;
        this.toDtoConverter = toDtoConverter;
    }

    @CrossOrigin
    @GetMapping
    public Collection<DTO> readAll(){
        return service.readAll().stream().map(toDtoConverter).toList();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<DTO> readOne(@PathVariable K id){
        Optional<E> optionalEntity = service.readById(id);
        return optionalEntity.map(entity -> ResponseEntity.ok(toDtoConverter.apply(entity)))
                .orElse(ResponseEntity.notFound().build());
    }
}
