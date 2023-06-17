package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.Offer;
import org.springframework.stereotype.Component;


/*
    Controller for offers
 */
@Component
public class OfferController extends AbstractController<OfferService,Offer,Long>{

    public OfferController(OfferService offerService){
        super(offerService);
    }
}
