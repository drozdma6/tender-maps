package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.AbstractService;
import cz.cvut.fit.bap.procurements.api.procurements_api.bussiness.CompanyService;
import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.CompanyDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;

/*
    Rest controller for companies
 */
@RestController
@RequestMapping("/companies")
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
    public List<CompanyDto> getSuppliers() {
        return ((CompanyService) service).getSuppliers().stream().map(toDtoConverter).toList();
    }

    /**
     * Gets all companies which have not yet won any procurements.
     *
     * @return companies which are not suppliers
     */
    @CrossOrigin
    @GetMapping("/non-suppliers")
    public List<CompanyDto> getCompaniesWithoutWonProcurements() {
        return ((CompanyService) service).getCompaniesWithoutWonProcurements().stream().map(toDtoConverter).toList();
    }
}
