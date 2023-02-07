package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Company implements DomainEntity<Long>{
    @Id
    private Long id;

    @Column
    private String name;

    @OneToMany
    private Set<Procurement> suppliedProcurements = new HashSet<>();

    @OneToMany
    private Set<Offer> offers = new HashSet<>();

    public Company(){
    }

    public Company(String name, Set<Procurement> suppliedProcurements, Set<Offer> offers){
        this.name = name;
        this.suppliedProcurements = suppliedProcurements;
        this.offers = offers;
    }

    public Set<Procurement> getSuppliedProcurements(){
        return suppliedProcurements;
    }

    public void setSuppliedProcurements(Set<Procurement> suppliedProcurements){
        this.suppliedProcurements = suppliedProcurements;
    }

    public Set<Offer> getOffers(){
        return offers;
    }

    public void setOffers(Set<Offer> offers){
        this.offers = offers;
    }

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
