package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for scrapping authority's completed list of procurements
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR/uzavrene-zakazky">contractor authority completed page</a>
 */
public class ContractorCompletedScrapper extends AbstractScrapper{
    public ContractorCompletedScrapper(Document document){
        super(document);
    }

    /**
     * Gets completed procurement system numbers made by provided authority name
     *
     * @param contractorAuthorityName name of contractor authority for filtering
     * @return List of completed procurement's system numbers
     */
    public List<String> getProcurementSystemNumbers(String contractorAuthorityName){
        Elements rows = document.select("tr.gov-table__row");
        List<String> systemNumbers = new ArrayList<>();
        for(Element row : rows){
            Element contractingAuthorityElem = row.selectFirst("td[data-title='Contracting authority']");
            Element procurementSystemNumberElem = row.selectFirst("td[data-title='NEN system number']");
            if(contractingAuthorityElem != null
                    && procurementSystemNumberElem != null
                    && contractorAuthorityName.equalsIgnoreCase(contractingAuthorityElem.text())){
                systemNumbers.add(procurementSystemNumberElem.text());
            }
        }
        return systemNumbers;
    }
}
