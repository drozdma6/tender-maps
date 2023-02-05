package cz.cvut.fit.bap.parser.jaxb;

import jakarta.xml.bind.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@XmlRootElement(name = "zakazka")
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class ProcurementJaxb{
    @XmlElement(name = "VZ")
    private PublicProcurementJaxb publicProcurementJaxb;
    @XmlElement(name = "dodavatel")
    private SupplierJaxb supplierJaxb;
    @XmlElement(name = "ucastnik")
    private ParticipantJaxb[] participantJaxbs;


    public PublicProcurementJaxb getPublicProcurementJaxb(){
        return publicProcurementJaxb;
    }

    public void setPublicProcurementJaxb(PublicProcurementJaxb publicProcurementJaxb){
        this.publicProcurementJaxb = publicProcurementJaxb;
    }

    public SupplierJaxb getSupplierJaxb(){
        return supplierJaxb;
    }

    public void setSupplierJaxb(SupplierJaxb supplierJaxb){
        this.supplierJaxb = supplierJaxb;
    }

    public ParticipantJaxb[] getParticipantJaxbs(){
        return participantJaxbs;
    }

    public void setParticipantJaxbs(ParticipantJaxb[] participantJaxbs){
        this.participantJaxbs = participantJaxbs;
    }

    @Override
    public String toString(){
        return "ProcurementJaxb{" + "publicProcurementJaxb=" + publicProcurementJaxb +
               ", supplierJaxb=" + supplierJaxb + ", participantJaxbs=" +
               Arrays.toString(participantJaxbs) + '}';
    }
}
