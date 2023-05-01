package cz.cvut.fit.bap.parser.domain;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/*
    Class represents contractor authority table
 */
@Entity
public class ContractorAuthority implements DomainEntity<Long>{
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "contractor_authority_name", unique = true)
    private String name;
    @OneToMany(mappedBy = "contractorAuthority")
    private Set<Procurement> procurements = new HashSet<>();

    @Column(name = "contractor_authority_profile", unique = true)
    private String profile;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "url")
    private String url;

    public Address getAddress(){
        return address;
    }

    public void setAddress(Address address){
        this.address = address;
    }

    public ContractorAuthority(String name, String profile, Address address, String url){
        this.name = name;
        this.profile = profile;
        this.address = address;
        this.url = url;
    }

    public ContractorAuthority(){
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getProfile(){
        return profile;
    }

    public void setProfile(String profile){
        this.profile = profile;
    }

    public Set<Procurement> getProcurements(){
        return procurements;
    }

    public void setProcurements(Set<Procurement> procurements){
        this.procurements = procurements;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof ContractorAuthority that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
