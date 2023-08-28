package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.ProcurementService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ProcurementDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.http.ResponseEntity;
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
     * Gets all procurements with supplier address latitude and longitude is not null and matching filtering.
     *
     * @param contractorAuthorityIds filtering by contractor authority ids
     * @param placesOfPerformance    filtering by places of performance
     * @return collection of procurement dtos with exact supplier address
     */
    @CrossOrigin
    @GetMapping("/suppliers/exact-address")
    public Collection<ProcurementDto> getProcurementsWithExactAddress(@RequestParam Optional<List<String>> placesOfPerformance,
                                                                      @RequestParam Optional<List<Long>> contractorAuthorityIds) {
        return ((ProcurementService) service).getProcurementsWithExactAddress(placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList()))
                .stream().map(toDtoConverter).toList();
    }

    /**
     * Gets procurements supplied by provided company id.
     *
     * @param supplierId id of supplier
     * @return procurements supplied by company id.
     */
    @CrossOrigin
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Collection<ProcurementDto>> getProcurementsBySupplierId(@PathVariable Long supplierId) {
        List<ProcurementDto> procurements = ((ProcurementService) service).getProcurementsBySupplierId(supplierId)
                .stream()
                .map(toDtoConverter)
                .toList();
        return ResponseEntity.ok(procurements);
    }
}
