package cz.cvut.fit.bap.parser.controller.dto;

import java.time.LocalDate;

public record ProcurementDetailDto(
        String procurementName,
        String placeOfPerformance,
        LocalDate dateOfPublication
){
}
