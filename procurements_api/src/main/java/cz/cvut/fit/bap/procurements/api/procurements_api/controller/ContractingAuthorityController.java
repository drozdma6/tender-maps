package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ContractingAuthorityDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractingAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.function.Function;

@RestController
@RequestMapping("/api/authorities")
public class ContractingAuthorityController extends AbstractController<ContractingAuthority, Long, ContractingAuthorityDto> {
    protected ContractingAuthorityController(AbstractService<ContractingAuthority, Long> service, Function<ContractingAuthority, ContractingAuthorityDto> toDtoConverter) {
        super(service, toDtoConverter);
    }

    /**
     * Gets all contracting authorities.
     *
     * @return dtos of all contracting authorities.
     */
    @CrossOrigin
    @GetMapping
    public Collection<ContractingAuthorityDto> readAll() {
        return service.readAll().stream().map(toDtoConverter).toList();
    }
}
