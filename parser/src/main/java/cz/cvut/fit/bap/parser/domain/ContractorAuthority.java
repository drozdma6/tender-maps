package cz.cvut.fit.bap.parser.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ContractorAuthority implements DomainEntity<Long>{
    @Id
    private Long id;
    @Column
    private String name;
    @OneToMany
    private Set<Procurement> procurements = new HashSet<>();

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
