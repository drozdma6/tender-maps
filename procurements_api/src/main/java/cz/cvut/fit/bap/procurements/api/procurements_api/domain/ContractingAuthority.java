package cz.cvut.fit.bap.procurements.api.procurements_api.domain;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/*
    Class represents contracting authority table
 */
@Entity
public class ContractingAuthority implements DomainEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "contracting_authority_name", unique = true)
    private String name;
    @OneToMany(mappedBy = "contractingAuthority")
    private Set<Procurement> procurements = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "url")
    private String url;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ContractingAuthority() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Procurement> getProcurements() {
        return procurements;
    }

    public void setProcurements(Set<Procurement> procurements) {
        this.procurements = procurements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ContractingAuthority that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
