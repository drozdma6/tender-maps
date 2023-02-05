package cz.cvut.fit.bap.parser.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Component;

@XmlRootElement(name = "dodavatel")
@XmlAccessorType(XmlAccessType.FIELD)
@Component
public class SupplierJaxb{
    @XmlElement(name = "nazev_dodavatele")
    private String name;
    @XmlElement(name = "ico")
    private String identificationNumber;
    @XmlElement(name = "zeme_sidla_dodavatele")
    private String supplierCountry;
    @XmlElement(name = "cena_celkem_dle_smlouvy_bez_DPH")
    private String priceWithTaxes;

    public void setPriceWithTaxes(String priceWithTaxes){
        this.priceWithTaxes = priceWithTaxes;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setIdentificationNumber(String identificationNumber){
        this.identificationNumber = identificationNumber;
    }

    public void setSupplierCountry(String supplierCountry){
        this.supplierCountry = supplierCountry;
    }

    public String getName(){
        return name;
    }

    public String getIdentificationNumber(){
        return identificationNumber;
    }

    public String getSupplierCountry(){
        return supplierCountry;
    }

    public String getPriceWithTaxes(){
        return priceWithTaxes;
    }

    @Override
    public String toString(){
        return "SupplierJaxb{" + "name='" + name + '\'' + ", identificationNumber='" +
               identificationNumber + '\'' + ", supplierCountry='" + supplierCountry + '\'' +
               ", priceWithTaxes='" + priceWithTaxes + '\'' + '}';
    }
}
