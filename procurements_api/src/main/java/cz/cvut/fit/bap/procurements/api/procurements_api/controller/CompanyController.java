package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.CompanyService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.CompanyDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/*
    Rest controller for companies
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController extends AbstractController<Company, Long, CompanyDto> {
    protected CompanyController(AbstractService<Company, Long> service, Function<Company, CompanyDto> toDtoConverter) {
        super(service, toDtoConverter);
    }

    /**
     * Gets all companies which match filtering
     *
     * @param placesOfPerformance    of supplied procurements or of procurements for which company made offers for
     * @param contractorAuthorityIds of supplied procurements or of procurements for which company made offers for
     * @param hasExactAddress        if company has exact geolocation, default is true
     * @param isSupplier             mandatory parameter, describing whether company has already supplied some procurements
     * @return suppliers which match filtering
     */
    @CrossOrigin
    @GetMapping
    public List<CompanyDto> readAll(@RequestParam Optional<List<String>> placesOfPerformance,
                                    @RequestParam Optional<List<Long>> contractorAuthorityIds,
                                    @RequestParam Optional<Boolean> hasExactAddress,
                                    @RequestParam Boolean isSupplier) {
        return ((CompanyService) service).readAll(
                        placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList()),
                        hasExactAddress.orElse(null), isSupplier)
                .stream()
                .map(toDtoConverter)
                .toList();
    }
}
