package cz.cvut.fit.bap.parser.domain;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

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

    public ContractorAuthority(String name, String profile){
        this.name = name;
        this.profile = profile;
    }

    public ContractorAuthority(){
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
}
