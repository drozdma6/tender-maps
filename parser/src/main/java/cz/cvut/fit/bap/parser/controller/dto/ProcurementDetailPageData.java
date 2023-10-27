package cz.cvut.fit.bap.parser.controller.dto;

import java.time.LocalDate;

/*
    Record representing procurement data from procurement detail page
 */
public record ProcurementDetailPageData(
        String procurementName,
        String placeOfPerformance,
        LocalDate dateOfPublication,
        String contractingAuthorityName,
        String contractingAuthorityUrl,
        String type,
        String typeOfProcedure,
        String publicContractRegime,
        LocalDate bidsSubmissionDeadline,
        String codeFromNipezCodeList,
        String nameFromNipezCodeList
){
}
