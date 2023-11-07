package cz.cvut.fit.bap.parser.controller.data;

import java.util.List;

/*
    Data scrapped from procurement list page
 */
public record ProcurementListPageData(
        List<String> systemNumbers
) {
}
