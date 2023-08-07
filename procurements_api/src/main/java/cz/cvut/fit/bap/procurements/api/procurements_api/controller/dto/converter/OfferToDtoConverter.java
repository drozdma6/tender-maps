package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.OfferDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Offer;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Domain entity to dto converter
 */
@Component
public class OfferToDtoConverter implements Function<Offer, OfferDto> {
    private final ProcurementToDtoConverter procurementToDtoConverter;

    public OfferToDtoConverter(ProcurementToDtoConverter procurementToDtoConverter) {
        this.procurementToDtoConverter = procurementToDtoConverter;
    }

    /**
     * Converts offer to dto
     *
     * @param offer which is supposed to be converted
     * @return offer dto
     */
    @Override
    public OfferDto apply(Offer offer) {
        return new OfferDto(offer.getId(), offer.getPrice(), procurementToDtoConverter.apply(offer.getProcurement()), offer.getCompany().getId());
    }
}
