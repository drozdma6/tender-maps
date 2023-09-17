package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class represents procurement data transfer object
 */
public class ProcurementDto {
    private final Long id;
    private final String name;
    private final CompanyDto supplier;
    private final BigDecimal contractPrice;
    private final String placeOfPerformance;
    private final LocalDate dateOfPublication;
    private final String systemNumber;
    private final String contractorAuthorityName;

    public ProcurementDto(Long id, String name, BigDecimal contractPrice, String placeOfPerformance,
                          LocalDate dateOfPublication, String systemNumber, CompanyDto supplier, String contractorAuthorityName) {
        this.id = id;
        this.name = name;
        this.contractPrice = contractPrice;
        this.placeOfPerformance = placeOfPerformance;
        this.dateOfPublication = dateOfPublication;
        this.systemNumber = systemNumber;
        this.supplier = supplier;
        this.contractorAuthorityName = contractorAuthorityName;
    }

    public String getContractorAuthorityName() {
        return contractorAuthorityName;
    }

    public CompanyDto getSupplier() {
        return supplier;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getContractPrice() {
        return contractPrice;
    }

    public String getPlaceOfPerformance() {
        return placeOfPerformance;
    }

    public LocalDate getDateOfPublication() {
        return dateOfPublication;
    }

    public String getSystemNumber() {
        return systemNumber;
    }
}