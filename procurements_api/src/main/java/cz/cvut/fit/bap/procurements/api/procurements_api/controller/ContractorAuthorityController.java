package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractorAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.function.Function;

@RestController
@RequestMapping("/api/authorities")
public class ContractorAuthorityController extends AbstractController<ContractorAuthority, Long, ContractorAuthorityDto> {
    protected ContractorAuthorityController(AbstractService<ContractorAuthority, Long> service, Function<ContractorAuthority, ContractorAuthorityDto> toDtoConverter) {
        super(service, toDtoConverter);
    }

    /**
     * Gets all contractor authorities.
     *
     * @return dtos of all contractor authorities.
     */
    @CrossOrigin
    @GetMapping
    public Collection<ContractorAuthorityDto> readAll() {
        return service.readAll().stream().map(toDtoConverter).toList();
    }
}
