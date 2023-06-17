package cz.cvut.fit.bap.parser.controller.dto;

import cz.cvut.fit.bap.parser.domain.ContractorAuthority;

import java.time.LocalDate;

/*
    Record representing procurement data from procurement detail page
 */
public record ProcurementDetailDto(
        String procurementName,
        String placeOfPerformance,
        LocalDate dateOfPublication,
        ContractorAuthority contractorAuthority
){
}
