package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

    public Procurement(){
    }

    public Procurement(String name, Company supplier, ContractorAuthority contractorAuthority,
                       BigDecimal contractPrice, String placeOfPerformance){
        this.name = name;
        this.supplier = supplier;
        this.contractorAuthority = contractorAuthority;
        this.contractPrice = contractPrice;
        this.placeOfPerformance = placeOfPerformance;
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
}
