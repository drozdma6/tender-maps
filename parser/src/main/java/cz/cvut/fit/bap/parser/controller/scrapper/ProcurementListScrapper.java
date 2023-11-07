package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.ProcurementListPageData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for scrapping procurement system numbers from table
 *
 * @see <a href="https://nen.nipez.cz/en/verejne-zakazky/p:vz:stavZP=zadana,plneni&page=1">Procurements list page</a>
 */
public class ProcurementListScrapper extends AbstractScrapper<ProcurementListPageData> {
    public ProcurementListScrapper(Document document){
        super(document);
    }

    @Override
    public ProcurementListPageData getPageData() {
        return new ProcurementListPageData(getProcurementSystemNumbers());
    }

    /**
     * Gets procurement system numbers from document
     *
     * @return list of procurement system numbers
     */
    private List<String> getProcurementSystemNumbers() {
        List<String> procurements = new ArrayList<>();
        Elements rows = document.select(".gov-table__row");
        for(Element row : rows){
            String procurementSystemNumber = row.select(".gov-table__cell--second.gov-table__cell").text();
            procurements.add(procurementSystemNumber);
        }
        return procurements;
    }

}
