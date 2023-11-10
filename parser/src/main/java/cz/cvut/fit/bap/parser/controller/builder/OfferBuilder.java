package cz.cvut.fit.bap.parser.controller.builder;

import cz.cvut.fit.bap.parser.controller.data.OfferData;
import cz.cvut.fit.bap.parser.controller.data.OfferDetailPageData;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.Procurement;

import java.math.BigDecimal;

/*
    Builder for offers
 */
public class OfferBuilder implements Builder<Long, Offer> {
    private final BigDecimal price;
    private final BigDecimal priceVAT;
    private final Boolean isRejectedDueTooLow;
    private final Boolean isWithdrawn;
    private final Boolean isAssociationOfSuppliers;
    private Procurement procurement;
    private Company company;

    public OfferBuilder(OfferData offerData, OfferDetailPageData offerDetailPageData) {
        this.price = offerData.price();
        this.priceVAT = offerData.priceVAT();
        this.isRejectedDueTooLow = offerDetailPageData.isRejectedDueTooLow();
        this.isWithdrawn = offerDetailPageData.isWithdrawn();
        this.isAssociationOfSuppliers = offerDetailPageData.isAssociationOfSuppliers();
    }

    public Company getCompany() {
        return company;
    }

    public OfferBuilder company(Company company) {
        this.company = company;
        return this;
    }

    public OfferBuilder procurement(Procurement procurement) {
        this.procurement = procurement;
        return this;
    }

    public Offer build() {
        return new Offer(price,
                priceVAT,
                procurement,
                company,
                isRejectedDueTooLow,
                isWithdrawn,
                isAssociationOfSuppliers
        );
    }
}
