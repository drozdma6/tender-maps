package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.ProcurementService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ProcurementDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.function.Function;

@RestController
@RequestMapping("/procurements")
public class ProcurementController extends AbstractController<Procurement,Long,ProcurementDto>{
    protected ProcurementController(AbstractService<Procurement,Long> service, Function<Procurement,ProcurementDto> toDtoConverter){
        super(service, toDtoConverter);
    }

    /**
     * Gets all procurements with exact supplier address.
     *
     * @return collection of procurement dtos with exact supplier address
     */
    @CrossOrigin
    @GetMapping("/exact-address-supplier")
    public Collection<ProcurementDto> getProcurementsWithExactAddress(){
        Collection<Procurement> procurements = ((ProcurementService) service).getProcurementsWithExactAddress();
        return procurements.stream().map(toDtoConverter).toList();
    }
}
