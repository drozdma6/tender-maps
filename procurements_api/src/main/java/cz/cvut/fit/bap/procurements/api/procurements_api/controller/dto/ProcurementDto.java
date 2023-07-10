package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class represents procurement dto
 */
public class ProcurementDto{
    private Long id;
    private String name;
    private CompanyDto supplier;
    private BigDecimal contractPrice;
    private String placeOfPerformance;
    private LocalDate dateOfPublication;
    private String systemNumber;

    public ProcurementDto(Long id, String name, BigDecimal contractPrice, String placeOfPerformance,
                          LocalDate dateOfPublication, String systemNumber, CompanyDto supplier){
        this.id = id;
        this.name = name;
        this.contractPrice = contractPrice;
        this.placeOfPerformance = placeOfPerformance;
        this.dateOfPublication = dateOfPublication;
        this.systemNumber = systemNumber;
        this.supplier = supplier;
    }

    public CompanyDto getSupplier(){
        return supplier;
    }

    public void setSupplier(CompanyDto supplier){
        this.supplier = supplier;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public BigDecimal getContractPrice(){
        return contractPrice;
    }

    public void setContractPrice(BigDecimal contractPrice){
        this.contractPrice = contractPrice;
    }

    public String getPlaceOfPerformance(){
        return placeOfPerformance;
    }

    public void setPlaceOfPerformance(String placeOfPerformance){
        this.placeOfPerformance = placeOfPerformance;
    }

    public LocalDate getDateOfPublication(){
        return dateOfPublication;
    }

    public void setDateOfPublication(LocalDate dateOfPublication){
        this.dateOfPublication = dateOfPublication;
    }

    public String getSystemNumber(){
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber){
        this.systemNumber = systemNumber;
    }
}
