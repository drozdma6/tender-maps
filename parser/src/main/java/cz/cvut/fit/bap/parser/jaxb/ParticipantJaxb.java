package cz.cvut.fit.bap.parser.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Component;

@XmlRootElement(name = "ucastnik")
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class ParticipantJaxb{
    @XmlElement(name = "nazev_ucastnika")
    private String name;
    @XmlElement(name = "ico")
    private String identificationNumber;
    @XmlElement(name = "zeme_sidla")
    private String supplierCountry;
    @XmlElement(name = "cena_bez_dph")
    private String priceWithTaxes;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getIdentificationNumber(){
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber){
        this.identificationNumber = identificationNumber;
    }

    public String getSupplierCountry(){
        return supplierCountry;
    }

    public void setSupplierCountry(String supplierCountry){
        this.supplierCountry = supplierCountry;
    }

    public String getPriceWithTaxes(){
        return priceWithTaxes;
    }

    public void setPriceWithTaxes(String priceWithTaxes){
        this.priceWithTaxes = priceWithTaxes;
    }

    @Override
    public String toString(){
        return "ParticipantJaxb{" + "name='" + name + '\'' + ", identificationNumber='" +
               identificationNumber + '\'' + ", supplierCountry='" + supplierCountry + '\'' +
               ", priceWithTaxes='" + priceWithTaxes + '\'' + '}';
    }
}