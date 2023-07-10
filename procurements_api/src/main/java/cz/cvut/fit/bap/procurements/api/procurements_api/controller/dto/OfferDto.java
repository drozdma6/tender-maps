package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

import java.math.BigDecimal;

/**
 * Class represents offer dto
 */
public class OfferDto{
    private Long id;
    private BigDecimal price;
    private Long procurementId;
    private Long companyId;

    public OfferDto(Long id, BigDecimal price, Long procurementId, Long companyId){
        this.id = id;
        this.price = price;
        this.procurementId = procurementId;
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

    public Long getProcurementId(){
        return procurementId;
    }

    public void setProcurementId(Long procurementId){
        this.procurementId = procurementId;
    }

    public Long getCompanyId(){
        return companyId;
    }

    public void setCompanyId(Long companyId){
        this.companyId = companyId;
    }
}
