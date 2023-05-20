package cz.cvut.fit.bap.parser.controller.dto;

import java.util.List;
import java.util.Map;

public record ProcurementResultDto(
        List<OfferDto> participants,
        Map<String,OfferDto> suppliersMap){
}