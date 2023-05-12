package cz.cvut.fit.bap.parser.controller.dto;

import java.util.HashMap;
import java.util.List;


public record ProcurementResultDto(
        List<CompanyDto> participants,
        HashMap<String,CompanyDto> suppliersMap){
}