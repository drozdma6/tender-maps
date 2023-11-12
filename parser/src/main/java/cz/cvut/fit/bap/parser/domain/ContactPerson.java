package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

@Entity
public class ContactPerson implements DomainEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @Column
    private String surname;
    @Column
    private String email;
    @ManyToOne
    @JoinColumn(name = "contracting_authority_id")
    private ContractingAuthority contractingAuthority;

    public ContactPerson() {
    }

    public ContactPerson(String name, String surname, String email, ContractingAuthority contractingAuthority) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.contractingAuthority = contractingAuthority;
    }

    public ContractingAuthority getContractingAuthority() {
        return contractingAuthority;
    }

    public void setContractingAuthority(ContractingAuthority contractingAuthority) {
        this.contractingAuthority = contractingAuthority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
