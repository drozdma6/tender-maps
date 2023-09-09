package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.OfferService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.OfferDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/*
    Rest controller for offers.
 */
@RestController
@RequestMapping("/api/offers")
public class OfferController extends AbstractController<Offer, Long, OfferDto> {
    protected OfferController(AbstractService<Offer, Long> service, Function<Offer, OfferDto> toDtoConverter) {
        super(service, toDtoConverter);
    }

    /**
     * Gets all offers created by provided companyID matching optional filtering.
     *
     * @param companyId              of searched company
     * @param placesOfPerformance    filtering by places of performance
     * @param contractorAuthorityIds filtering by contracting authority ids
     * @return offers created by company matching filtering
     */
    @CrossOrigin
    @GetMapping
    public ResponseEntity<Collection<OfferDto>> getOffersByCompanyId(@RequestParam Optional<List<String>> placesOfPerformance,
                                                                     @RequestParam Optional<List<Long>> contractorAuthorityIds,
                                                                     @RequestParam Long companyId) {
        List<OfferDto> offerDtos = ((OfferService) service).getOffersByCompanyId(
                        companyId,
                        placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList())
                )
                .stream()
                .map(toDtoConverter)
                .toList();
        return ResponseEntity.ok(offerDtos);
    }
}
