package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.ProcurementService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ProcurementDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/*
    Rest controller for procurements
 */
@RestController
@RequestMapping("/api/procurements")
public class ProcurementController extends AbstractController<Procurement, Long, ProcurementDto> {
    protected ProcurementController(AbstractService<Procurement, Long> service, Function<Procurement, ProcurementDto> toDtoConverter) {
        super(service, toDtoConverter);
    }

    /**
     * Gets all procurements matching filtering by parameters.
     *
     * @param contractorAuthorityIds filtering by contractor authority ids
     * @param placesOfPerformance    filtering by places of performance
     * @param supplierHasExactAddress filtering whether supplier's address is exact (latitude and longitude is not null)
     * @return procurements matching filtering
     */
    @CrossOrigin
    @GetMapping
    public Collection<ProcurementDto> readAll(@RequestParam Optional<List<String>> placesOfPerformance,
                                              @RequestParam Optional<List<Long>> contractorAuthorityIds,
                                              @RequestParam Optional<Boolean> supplierHasExactAddress,
                                              @RequestParam Optional<Long> supplierId) {
        return ((ProcurementService) service).readAll(
                        placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList()),
                        supplierHasExactAddress.orElse(null),
                        supplierId.orElse(null))
                .stream().map(toDtoConverter).toList();
    }
}
