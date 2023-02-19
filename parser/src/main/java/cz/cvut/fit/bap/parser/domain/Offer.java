package cz.cvut.fit.bap.parser.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Offer implements DomainEntity<OfferId>{
    @EmbeddedId
    private OfferId id;

    @Column(precision = 14, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("procurementId")
    @JoinColumn(name = "procurement_id")
    private Procurement procurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    private Company company;

    public Offer(){
    }

    public Offer(OfferId id, BigDecimal price, Procurement procurement, Company company){
        this.id = id;
        this.price = price;
        this.procurement = procurement;
        this.company = company;
    }

    public Offer(OfferId id, Procurement procurement, Company company){
        this.id = id;
        this.procurement = procurement;
        this.company = company;
    }

    public Procurement getProcurement(){
        return procurement;
    }

    public void setProcurement(Procurement procurement){
        this.procurement = procurement;
    }

    public Company getCompany(){
        return company;
    }

    public void setCompany(Company company){
        this.company = company;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public Company getSupplier(){
        return company;
    }

    public void setSupplier(Company company){
        this.company = company;
    }


    @Override
    public void setId(OfferId offerId){
        this.id = offerId;
    }

    @Override
    public OfferId getId(){
        return id;
    }

    @Override
    public String toString(){
        return "Offer{" + "id=" + id + ", price=" + price + ", procurement=" + procurement +
               ", company=" + company + '}';
    }
}
