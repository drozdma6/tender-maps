package cz.cvut.fit.bap.parser.controller.builder;

import cz.cvut.fit.bap.parser.controller.data.ContractData;
import cz.cvut.fit.bap.parser.controller.data.ProcurementDetailPageData;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import cz.cvut.fit.bap.parser.domain.Procurement;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
    Builder for procurements
 */
public class ProcurementBuilder implements Builder<Long, Procurement> {
    private final String name;
    private final Company supplier;
    private final Boolean isAssociationOfSuppliers;
    private final ContractingAuthority contractingAuthority;
    private final BigDecimal contractPrice;
    private final BigDecimal contractPriceVAT;
    private final BigDecimal contractPriceWithAmendments;
    private final BigDecimal contractPriceWithAmendmentsVAT;
    private final String placeOfPerformance;
    private final LocalDate dateOfPublication;
    private final String systemNumber;
    private final LocalDate dateOfContractClose;
    private final String type;
    private final String typeOfProcedure;
    private final String publicContractRegime;
    private final LocalDate bidsSubmissionDeadline;
    private final String codeFromNipezCodeList;
    private final String nameFromNipezCodeList;

    public ProcurementBuilder(ProcurementDetailPageData procurementDetailPageData,
                              ContractData contractData,
                              Company supplier,
                              ContractingAuthority contractingAuthority,
                              Boolean isAssociationOfSuppliers,
                              String systemNumber) {
        this.name = procurementDetailPageData.procurementName();
        this.supplier = supplier;
        this.isAssociationOfSuppliers = isAssociationOfSuppliers;
        this.contractingAuthority = contractingAuthority;
        this.contractPrice = contractData.price();
        this.contractPriceVAT = contractData.priceVAT();
        this.contractPriceWithAmendments = contractData.priceWithAmend();
        this.contractPriceWithAmendmentsVAT = contractData.priceWithAmendVAT();
        this.placeOfPerformance = procurementDetailPageData.placeOfPerformance();
        this.dateOfPublication = procurementDetailPageData.dateOfPublication();
        this.systemNumber = systemNumber;
        this.dateOfContractClose = contractData.contractDate();
        this.type = procurementDetailPageData.type();
        this.typeOfProcedure = procurementDetailPageData.typeOfProcedure();
        this.publicContractRegime = procurementDetailPageData.publicContractRegime();
        this.bidsSubmissionDeadline = procurementDetailPageData.bidsSubmissionDeadline();
        this.codeFromNipezCodeList = procurementDetailPageData.codeFromNipezCodeList();
        this.nameFromNipezCodeList = procurementDetailPageData.nameFromNipezCodeList();
    }

    public Procurement build() {
        return new Procurement(name,
                supplier,
                isAssociationOfSuppliers,
                contractingAuthority,
                contractPrice,
                contractPriceVAT,
                contractPriceWithAmendments,
                contractPriceWithAmendmentsVAT,
                placeOfPerformance,
                dateOfPublication,
                systemNumber,
                dateOfContractClose,
                type,
                typeOfProcedure,
                publicContractRegime,
                bidsSubmissionDeadline,
                codeFromNipezCodeList,
                nameFromNipezCodeList
        );
    }
}
