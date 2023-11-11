package cz.cvut.fit.bap.parser.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;


/*
    Class represents offer from certain company to certain procurement
 */
@Entity
public class Offer implements DomainEntity<Long>{
    @Id
    @GeneratedValue
    private Long id;

    @Column(precision = 18, scale = 2)
    private BigDecimal price;

    @Column(precision = 18, scale = 2, name = "price_VAT")
    private BigDecimal priceVAT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procurement_id")
    private Procurement procurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "is_rejected_due_too_low")
    private Boolean isRejectedDueTooLow;

    @Column(name = "is_withdrawn")
    private Boolean isWithdrawn;

    @Column(name = "is_association_of_suppliers")
    private Boolean isAssociationOfSuppliers;

    public Offer(){
    }

    public Offer(BigDecimal price,
                 BigDecimal priceVAT,
                 Procurement procurement,
                 Company company,
                 Boolean isRejectedDueTooLow,
                 Boolean isWithdrawn,
                 Boolean isAssociationOfSuppliers) {
        this.price = price;
        this.priceVAT = priceVAT;
        this.procurement = procurement;
        this.company = company;
        this.isRejectedDueTooLow = isRejectedDueTooLow;
        this.isWithdrawn = isWithdrawn;
        this.isAssociationOfSuppliers = isAssociationOfSuppliers;
    }

    public BigDecimal getPriceVAT() {
        return priceVAT;
    }

    public void setPriceVAT(BigDecimal priceVAT) {
        this.priceVAT = priceVAT;
    }

    public Boolean getWithdrawn() {
        return isWithdrawn;
    }

    public void setWithdrawn(Boolean withdrawn) {
        isWithdrawn = withdrawn;
    }

    public Boolean getAssociationOfSuppliers() {
        return isAssociationOfSuppliers;
    }

    public void setAssociationOfSuppliers(Boolean associationOfSuppliers) {
        isAssociationOfSuppliers = associationOfSuppliers;
    }

    public Boolean getRejectedDueTooLow() {
        return isRejectedDueTooLow;
    }

    public void setRejectedDueTooLow(Boolean rejectedDueTooLow) {
        isRejectedDueTooLow = rejectedDueTooLow;
    }

    public Procurement getProcurement(){
        return procurement;
    }

    public void setProcurement(Procurement procurement){
        this.procurement = procurement;
    }

    public Company getCompany(){
        return company;
    }

    public void setCompany(Company company){
        this.company = company;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(!(o instanceof Offer offer))
            return false;
        return Objects.equals(id, offer.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
