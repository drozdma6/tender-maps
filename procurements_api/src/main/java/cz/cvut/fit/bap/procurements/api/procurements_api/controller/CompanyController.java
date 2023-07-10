package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.CompanyDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequestMapping("/companies")
public class CompanyController extends AbstractController<Company, Long,CompanyDto>{
    protected CompanyController(AbstractService<Company,Long> service, Function<Company,CompanyDto> toDtoConverter){
        super(service, toDtoConverter);
    }
}
