package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
    Class represents procurement table
 */
@Entity
public class Procurement implements DomainEntity<Long>{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "procurement_name")
    private String name;

    @OneToMany(mappedBy = "procurement")
    private Set<Offer> offers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Company supplier;

    @ManyToOne
    @JoinColumn(name = "contractor_authority_id")
    private ContractorAuthority contractorAuthority;

    @Column(name = "contract_price", precision = 14, scale = 2)
    private BigDecimal contractPrice;

    @Column(name = "place_of_performance")
    private String placeOfPerformance;

    @Column(name = "date_of_publication")
    private LocalDate dateOfPublication;

    @Column(name = "system_number")
    private String systemNumber;

    public Procurement(){
    }

    public Procurement(String name, Company supplier, ContractorAuthority contractorAuthority,
                       BigDecimal contractPrice, String placeOfPerformance, LocalDate dateOfPublication, String systemNumber){
        this.name = name;
        this.supplier = supplier;
        this.contractorAuthority = contractorAuthority;
        this.contractPrice = contractPrice;
        this.placeOfPerformance = placeOfPerformance;
        this.dateOfPublication = dateOfPublication;
        this.systemNumber = systemNumber;
    }

    public String getSystemNumber(){
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber){
        this.systemNumber = systemNumber;
    }

    public LocalDate getDateOfPublication(){
        return dateOfPublication;
    }

    public void setDateOfPublication(LocalDate dateOfPublication){
        this.dateOfPublication = dateOfPublication;
    }

    public String getPlaceOfPerformance(){
        return placeOfPerformance;
    }

    public void setPlaceOfPerformance(String placeOfPerformance){
        this.placeOfPerformance = placeOfPerformance;
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

    public Company getSupplier(){
        return supplier;
    }

    public void setSupplier(Company supplier){
        this.supplier = supplier;
    }

    public BigDecimal getContractPrice(){
        return contractPrice;
    }

    public void setContractPrice(BigDecimal contractPrice){
        this.contractPrice = contractPrice;
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

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Procurement that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
