package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.OfferService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.OfferDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/*
    Rest controller for offers.
 */
@RestController
@RequestMapping("/offers")
public class OfferController extends AbstractController<Offer,Long,OfferDto>{
    protected OfferController(AbstractService<Offer,Long> service, Function<Offer,OfferDto> toDtoConverter){
        super(service, toDtoConverter);
    }

    /**
     * Gets all offers created by provided company id.
     *
     * @param companyId id of company
     * @return offers created by companyId.
     */
    @CrossOrigin
    @GetMapping("/companies/{companyId}")
    public ResponseEntity<Collection<OfferDto>> getOffersByCompanyId(@PathVariable Long companyId){
        List<OfferDto> offerDtos = ((OfferService) service).getOffersByCompanyId(companyId)
                .stream()
                .map(toDtoConverter)
                .toList();
        if (offerDtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(offerDtos);
    }
}
