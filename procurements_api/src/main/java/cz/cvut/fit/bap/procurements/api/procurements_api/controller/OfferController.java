package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.OfferDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequestMapping("/offers")
public class OfferController extends AbstractController<Offer,Long,OfferDto>{
    protected OfferController(AbstractService<Offer,Long> service, Function<Offer,OfferDto> toDtoConverter){
        super(service, toDtoConverter);
    }
}
