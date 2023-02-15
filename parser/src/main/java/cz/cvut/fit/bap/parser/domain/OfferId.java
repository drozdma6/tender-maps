package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class OfferId implements Serializable{
    private Long procurementId;
    private Long companyId;

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
        companyId = companyId;
    }
}
