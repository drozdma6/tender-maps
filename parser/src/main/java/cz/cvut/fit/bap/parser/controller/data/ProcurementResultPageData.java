package cz.cvut.fit.bap.parser.controller.data;

import java.util.List;

public record ProcurementResultPageData(
        List<OfferData> participants,
        List<ContractData> suppliers) {
}