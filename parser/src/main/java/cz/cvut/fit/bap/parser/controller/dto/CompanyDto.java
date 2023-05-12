package cz.cvut.fit.bap.parser.controller.dto;

import java.math.BigDecimal;

/*
    Class represents Company information
 */
public class CompanyDto{
    private BigDecimal contractPrice;
    private final String detailHref;
    private final String companyName;

    public CompanyDto(BigDecimal contractPrice, String detailHref, String companyName){
        this.contractPrice = contractPrice;
        this.detailHref = detailHref;
        this.companyName = companyName;
    }

    public String getCompanyName(){
        return companyName;
    }

    public BigDecimal getContractPrice(){
        return contractPrice;
    }

    public String getDetailHref(){
        return detailHref;
    }

    public void addContractPrice(BigDecimal price){
        if(this.contractPrice == null){
            this.contractPrice = price;
        }else{
            if(price != null){
                this.contractPrice = this.contractPrice.add(price);
            }
        }
    }
}