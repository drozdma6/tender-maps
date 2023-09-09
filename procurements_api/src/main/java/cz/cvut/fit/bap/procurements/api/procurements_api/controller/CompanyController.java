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
     * Gets all companies which are suppliers for at least one procurement and match filtering
     *
     * @param placesOfPerformance    of supplied procurements or of procurements for which company made offers for
     * @param contractorAuthorityIds of supplied procurements or of procurements for which company made offers for
     * @param hasExactAddress        if company has exact geolocation, default is true
     * @return suppliers which match filtering
     */
    @CrossOrigin
    @GetMapping("/suppliers")
    public List<CompanyDto> getSuppliers(@RequestParam Optional<List<String>> placesOfPerformance,
                                         @RequestParam Optional<List<Long>> contractorAuthorityIds,
                                         @RequestParam Optional<Boolean> hasExactAddress) {
        return ((CompanyService) service).getSuppliers(
                        placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList()),
                        hasExactAddress.orElse(null))
                .stream()
                .map(toDtoConverter)
                .toList();
    }

    /**
     * Gets all companies which are not yet suppliers and match filtering
     *
     * @param placesOfPerformance    of procurements for which company made offers for
     * @param contractorAuthorityIds of procurements for which company made offers for
     * @param hasExactAddress        if company has exact geolocation, default is true
     * @return non-suppliers which match filtering
     */
    @CrossOrigin
    @GetMapping("/non-suppliers")
    public List<CompanyDto> getNonSuppliers(@RequestParam Optional<List<String>> placesOfPerformance,
                                            @RequestParam Optional<List<Long>> contractorAuthorityIds,
                                            @RequestParam Optional<Boolean> hasExactAddress) {
        return ((CompanyService) service).getNonSuppliers(
                        placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList()),
                        hasExactAddress.orElse(null))
                .stream()
                .map(toDtoConverter)
                .toList();
    }
}
