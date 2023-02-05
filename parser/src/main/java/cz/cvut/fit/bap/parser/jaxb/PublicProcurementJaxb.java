package cz.cvut.fit.bap.parser.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Component;

import java.util.Date;

@XmlRootElement(name = "VZ")
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class PublicProcurementJaxb{
    @XmlElement(name = "nazev_vz")
    private String name;
    @XmlElement(name = "datum_cas_zverejneni")
    private Date dateOfRelease;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getDateOfRelease(){
        return dateOfRelease;
    }

    public void setDateOfRelease(Date dateOfRelease){
        this.dateOfRelease = dateOfRelease;
    }

    @Override
    public String toString(){
        return "PublicProcurementJaxb{" + "name='" + name + '\'' + ", dateOfRelease=" +
               dateOfRelease + '}';
    }
}
