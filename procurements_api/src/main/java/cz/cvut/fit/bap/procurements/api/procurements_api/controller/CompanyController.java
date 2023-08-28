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
     * Gets all companies which are suppliers for at least one procurement.
     *
     * @return suppliers
     */
    @CrossOrigin
    @GetMapping("/suppliers")
    public List<CompanyDto> getSuppliers(@RequestParam Optional<List<String>> placesOfPerformance,
                                         @RequestParam Optional<List<Long>> contractorAuthorityIds) {
        return ((CompanyService) service).getSuppliers(placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList()))
                .stream()
                .map(toDtoConverter)
                .toList();
    }

    /**
     * Gets all companies which have not yet won any procurements.
     *
     * @return companies which are not suppliers
     */
    @CrossOrigin
    @GetMapping("/non-suppliers")
    public List<CompanyDto> getNonSuppliers(@RequestParam Optional<List<String>> placesOfPerformance, @RequestParam Optional<List<Long>> contractorAuthorityIds) {
        return ((CompanyService) service).getNonSuppliers(
                        placesOfPerformance.orElse(Collections.emptyList()),
                        contractorAuthorityIds.orElse(Collections.emptyList()))
                .stream()
                .map(toDtoConverter)
                .toList();
    }
}
