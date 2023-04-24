package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.OfferId;
import cz.cvut.fit.bap.parser.domain.Procurement;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/*
    Controller for communication with offer service
 */
@Component
public class OfferController extends AbstractController<OfferService>{

    public OfferController(OfferService offerService){
        super(offerService);
    }

    /**
     * Saves offers.
     *
     * @param procurement on which offer was created
     * @param company     which created offer
     */
    public void saveOffer(BigDecimal price, Procurement procurement, Company company){
        OfferId offerId = new OfferId(procurement.getId(), company.getId());
        Offer offer = new Offer(offerId, price, procurement, company);
        service.create(offer);
    }
}
