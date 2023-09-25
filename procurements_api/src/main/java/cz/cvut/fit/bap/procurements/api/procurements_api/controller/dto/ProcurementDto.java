package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class represents procurement data transfer object
 */
public record ProcurementDto(
        Long id,
        String name,
        BigDecimal contractPrice,
        String placeOfPerformance,
        LocalDate dateOfPublication,
        String systemNumber,
        CompanyDto supplier,
        String contractorAuthorityName) {
}