package cz.cvut.fit.bap.parser.controller.dto;

import java.util.List;

public record ProcurementResultDto(
        List<OfferDto> participants,
        List<ContractData> suppliers) {
}