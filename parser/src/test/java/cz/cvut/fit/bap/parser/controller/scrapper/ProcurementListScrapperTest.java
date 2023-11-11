package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.ProcurementListPageData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ProcurementListScrapperTest {
    private final String documentStr = """
                <div class="gov-content-block" title="Supplier with Whom the Contract Has Been Entered into">
                    <table class="gov-table gov-table--tablet-block gov-sortable-table">
                        <tbody class="gov-table__body">
                            <tr class="gov-table__row">
                                <td class="gov-table__cell gov-table__cell--second"
                                    title="N006/23/V00013199" style="width:165px"
                                    data-title="NEN system number">N006/23/V00013199
                                </td>
                            </tr>
                            <tr class="gov-table__row">
                                <td class="gov-table__cell gov-table__cell--second"
                                    title="N006/22/V00002876" style="width:165px"
                                    data-title="NEN system number">N006/22/V00002876
                                </td>
                            </tr>
                            <tr class="gov-table__row">
                                <td class="gov-table__cell gov-table__cell--second"
                                    title="N006/23/V00013052" style="width:165px"
                                    data-title="NEN system number">N006/23/V00013052
                                </td>
                            </tr>
                      </tbody>
                </table>
            </div>
                """;
    private final String systemNumber0 = "N006/23/V00013199";
    private final String systemNumber1 = "N006/22/V00002876";
    private final String systemNumber2 = "N006/23/V00013052";
    ProcurementListPageData expectedData = new ProcurementListPageData(
            List.of(systemNumber0, systemNumber1, systemNumber2)
    );

    @Test
    void getProcurementDto() {
        Document document = Jsoup.parse(documentStr);
        ProcurementListScrapper procurementListScrapper = new ProcurementListScrapper(document);
        ProcurementListPageData actualData = procurementListScrapper.getPageData();
        Assertions.assertEquals(expectedData, actualData);
    }
}