package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/*
    Class represents procurement table
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"system_number", "supplier_id"})})
public class Procurement implements DomainEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "procurement_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Company supplier;

    @ManyToOne
    @JoinColumn(name = "contracting_authority_id")
    private ContractingAuthority contractingAuthority;

    @Column(name = "contract_price", precision = 14, scale = 2)
    private BigDecimal contractPrice;

    @Column(name = "place_of_performance")
    private String placeOfPerformance;

    @Column(name = "date_of_publication")
    private LocalDate dateOfPublication;

    @Column(name = "system_number")
    private String systemNumber;

    @Column(name = "date_of_contract_close")
    private LocalDate dateOfContractClose;

    @Column(name = "type")
    private String type;

    @Column(name = "type_of_procedure")
    private String typeOfProcedure;

    @Column(name = "public_contract_regime")
    private String publicContractRegime;

    @Column(name = "bids_submission_deadline")
    private LocalDate bidsSubmissionDeadline;

    @Column(name = "code_from_nipez_codelist")
    private String codeFromNipezCodeList;

    @Column(name = "name_from_nipez_codelist")
    private String nameFromNipezCodeList;

    public Procurement() {
    }

    public Procurement(String name, Company supplier, ContractingAuthority contractingAuthority,
                       BigDecimal contractPrice, String placeOfPerformance, LocalDate dateOfPublication, String systemNumber,
                       LocalDate dateOfContractClose, String type, String typeOfProcedure, String publicContractRegime,
                       LocalDate bidsSubmissionDeadline, String codeFromNipezCodeList, String nameFromNipezCodeList) {
        this.name = name;
        this.supplier = supplier;
        this.contractingAuthority = contractingAuthority;
        this.contractPrice = contractPrice;
        this.placeOfPerformance = placeOfPerformance;
        this.dateOfPublication = dateOfPublication;
        this.systemNumber = systemNumber;
        this.dateOfContractClose = dateOfContractClose;
        this.type = type;
        this.typeOfProcedure = typeOfProcedure;
        this.publicContractRegime = publicContractRegime;
        this.bidsSubmissionDeadline = bidsSubmissionDeadline;
        this.codeFromNipezCodeList = codeFromNipezCodeList;
        this.nameFromNipezCodeList = nameFromNipezCodeList;
    }

    public String getTypeOfProcedure() {
        return typeOfProcedure;
    }

    public void setTypeOfProcedure(String typeOfProcedure) {
        this.typeOfProcedure = typeOfProcedure;
    }

    public String getPublicContractRegime() {
        return publicContractRegime;
    }

    public void setPublicContractRegime(String publicContractRegime) {
        this.publicContractRegime = publicContractRegime;
    }

    public LocalDate getBidsSubmissionDeadline() {
        return bidsSubmissionDeadline;
    }

    public void setBidsSubmissionDeadline(LocalDate bidsSubmissionDeadline) {
        this.bidsSubmissionDeadline = bidsSubmissionDeadline;
    }

    public String getCodeFromNipezCodeList() {
        return codeFromNipezCodeList;
    }

    public void setCodeFromNipezCodeList(String codeFromNipezCodeList) {
        this.codeFromNipezCodeList = codeFromNipezCodeList;
    }

    public String getNameFromNipezCodeList() {
        return nameFromNipezCodeList;
    }

    public void setNameFromNipezCodeList(String nameFromNipezCodeList) {
        this.nameFromNipezCodeList = nameFromNipezCodeList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDateOfContractClose() {
        return dateOfContractClose;
    }

    public void setDateOfContractClose(LocalDate dateOfContractClose) {
        this.dateOfContractClose = dateOfContractClose;
    }

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }

    public LocalDate getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(LocalDate dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    public String getPlaceOfPerformance() {
        return placeOfPerformance;
    }

    public void setPlaceOfPerformance(String placeOfPerformance) {
        this.placeOfPerformance = placeOfPerformance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company getSupplier() {
        return supplier;
    }

    public void setSupplier(Company supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getContractPrice() {
        return contractPrice;
    }

    public void setContractPrice(BigDecimal contractPrice) {
        this.contractPrice = contractPrice;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public ContractingAuthority getContractingAuthority() {
        return contractingAuthority;
    }

    public void setContractingAuthority(ContractingAuthority contractingAuthority) {
        this.contractingAuthority = contractingAuthority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Procurement that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
