package cz.cvut.fit.bap.parser.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@XmlRootElement(name = "profil")
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class ProfilJaxb{
    @XmlElement(name = "zadavatel")
    private ContractingAuthorityJaxb contractingAuthority;
    @XmlElement(name = "zakazka")
    private ProcurementJaxb[] procurementJaxb;

    public ContractingAuthorityJaxb getContractingAuthority(){
        return contractingAuthority;
    }

    public void setContractingAuthority(ContractingAuthorityJaxb contractingAuthority){
        this.contractingAuthority = contractingAuthority;
    }

    public ProcurementJaxb[] getProcurementJaxb(){
        return procurementJaxb;
    }

    public void setProcurementJaxb(ProcurementJaxb[] procurementJaxb){
        this.procurementJaxb = procurementJaxb;
    }

    @Override
    public String toString(){
        return "ProfilJaxb{" + "contractingAuthority=" + contractingAuthority +
               ", procurementJaxb=" + Arrays.toString(procurementJaxb) + '}';
    }
}
