package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

import java.math.BigDecimal;

/**
 * Class represents offer data transfer object
 */
public class OfferDto{
    private final Long id;
    private final BigDecimal price;
    private final ProcurementDto procurement;
    private final Long companyId;

    public OfferDto(Long id, BigDecimal price, ProcurementDto procurement, Long companyId){
        this.id = id;
        this.price = price;
        this.procurement = procurement;
        this.companyId = companyId;
    }

    public Long getId(){
        return id;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public ProcurementDto getProcurement() {
        return procurement;
    }

    public Long getCompanyId(){
        return companyId;
    }
}
