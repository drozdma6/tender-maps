package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
    Class represents company table
 */
@Entity
public class Company implements DomainEntity<Long>{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "company_name", unique = true)
    private String name;

    @OneToMany(mappedBy = "supplier")
    private Set<Procurement> suppliedProcurements = new HashSet<>();

    @OneToMany(mappedBy = "company")
    private Set<Offer> offers = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "organisation_id", unique = true)
    private String organisationId;

    public Address getAddress(){
        return address;
    }

    public void setAddress(Address address){
        this.address = address;
    }

    public Company(){
    }

    public Company(String name, Address address, String organisationId){
        this.name = name;
        this.address = address;
        this.organisationId = organisationId;
    }

    public String getOrganisationId(){
        return organisationId;
    }

    public void setOrganisationId(String organisationId){
        this.organisationId = organisationId;
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

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Company company))
            return false;
        return Objects.equals(id, company.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    @Override
    public String toString(){
        return "Company{" + "id=" + id + ", name='" + name + '\'' + ", suppliedProcurements=" +
               suppliedProcurements + ", offers=" + offers + ", address=" + address + '}';
    }
}
