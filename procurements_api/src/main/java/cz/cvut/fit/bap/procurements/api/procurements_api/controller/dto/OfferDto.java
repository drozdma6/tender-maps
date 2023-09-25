package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

import java.math.BigDecimal;

/**
 * Class represents offer data transfer object
 */
public record OfferDto(
        Long id,
        BigDecimal price,
        ProcurementDto procurement,
        Long companyId) {
}
