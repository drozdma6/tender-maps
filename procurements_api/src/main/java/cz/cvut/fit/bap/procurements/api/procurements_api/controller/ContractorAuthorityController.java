package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractorAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequestMapping("/api/authorities")
public class ContractorAuthorityController extends AbstractController<ContractorAuthority, Long,ContractorAuthorityDto>{
    protected ContractorAuthorityController(AbstractService<ContractorAuthority,Long> service, Function<ContractorAuthority,ContractorAuthorityDto> toDtoConverter){
        super(service, toDtoConverter);
    }
}
