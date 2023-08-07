package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

import java.math.BigDecimal;

/**
 * Class represents offer data transfer object
 */
public class OfferDto{
    private Long id;
    private BigDecimal price;
    private ProcurementDto procurement;
    private Long companyId;

    public OfferDto(Long id, BigDecimal price, ProcurementDto procurement, Long companyId){
        this.id = id;
        this.price = price;
        this.procurement = procurement;
        this.companyId = companyId;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public ProcurementDto getProcurement() {
        return procurement;
    }

    public void setProcurement(ProcurementDto procurement) {
        this.procurement = procurement;
    }

    public Long getCompanyId(){
        return companyId;
    }

    public void setCompanyId(Long companyId){
        this.companyId = companyId;
    }
}
