package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.AddressDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Address;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequestMapping("/api/addresses")
public class AddressController extends AbstractController<Address, Long, AddressDto>{

    protected AddressController(AbstractService<Address,Long> service, Function<Address,AddressDto> toDtoConverter){
        super(service, toDtoConverter);
    }
}
