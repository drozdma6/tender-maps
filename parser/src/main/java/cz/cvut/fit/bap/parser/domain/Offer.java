package cz.cvut.fit.bap.parser.domain;


import jakarta.persistence.*;

@Entity
public class Offer implements DomainEntity<OfferId>{
    @EmbeddedId
    private OfferId id;

    @Column
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("procurementId")
    private Procurement procurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("procurementId")
    private Company company;

    public Offer(){
    }

    public Offer(Long price, Procurement procurement, Company company){
        this.price = price;
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

    public Long getPrice(){
        return price;
    }

    public void setPrice(Long price){
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
}
