package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.OfferDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OfferToDtoConverter implements Function<Offer,OfferDto>{
    @Override
    public OfferDto apply(Offer offer){
        return new OfferDto(offer.getId(), offer.getPrice(), offer.getProcurement().getId(), offer.getCompany().getId());
    }
}
