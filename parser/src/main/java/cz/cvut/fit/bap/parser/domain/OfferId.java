package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OfferId implements Serializable{
    private Long procurementId;
    private Long companyId;

    public OfferId(Long procurementId, Long companyId){
        this.procurementId = procurementId;
        this.companyId = companyId;
    }

    public OfferId(){
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

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof OfferId offerId))
            return false;
        return Objects.equals(procurementId, offerId.procurementId) &&
               Objects.equals(companyId, offerId.companyId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(procurementId, companyId);
    }
}
