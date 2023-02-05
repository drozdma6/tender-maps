package cz.cvut.fit.bap.parser.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Component;

@XmlRootElement(name = "zadavatel")
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class ContractingAuthorityJaxb{
    @XmlElement(name = "nazev_zadavatele")
    private String name;


    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return "ContractingAuthority{" + "name='" + name + '\'' + '}';
    }
}
