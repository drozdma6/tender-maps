package cz.cvut.fit.bap.parser.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;


/*
    Class represents offer from certain company to certain procurement
 */
@Entity
public class Offer implements DomainEntity<Long>{
    @Id
    @GeneratedValue
    private Long id;

    @Column(precision = 14, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procurement_id")
    private Procurement procurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    public Offer(){
    }

    public Offer(BigDecimal price, Procurement procurement, Company company){
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

    public BigDecimal getPrice(){
        return price;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(!(o instanceof Offer offer))
            return false;
        return Objects.equals(id, offer.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
