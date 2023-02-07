package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Procurement implements DomainEntity<Long>{
    @Id
    private Long id;
    @Column
    private String name;

    @OneToMany
    private Set<Offer> offers = new HashSet<>();

    @ManyToOne
    private Company supplier;

    @ManyToOne
    private ContractorAuthority contractorAuthority;

    public Procurement(){
    }

    public Procurement(String name, Set<Offer> offers, Company supplier,
                       ContractorAuthority contractorAuthority){
        this.name = name;
        this.offers = offers;
        this.supplier = supplier;
        this.contractorAuthority = contractorAuthority;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Set<Offer> getOffers(){
        return offers;
    }

    public void setOffers(Set<Offer> offers){
        this.offers = offers;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }

    @Override
    public Long getId(){
        return id;
    }

    public ContractorAuthority getContractorAuthority(){
        return contractorAuthority;
    }

    public void setContractorAuthority(ContractorAuthority contractorAuthority){
        this.contractorAuthority = contractorAuthority;
    }
}
