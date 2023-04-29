package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return document.select("tr.gov-table__row")
                .stream()
                .filter(row -> contractorAuthorityName.equalsIgnoreCase(
                        Optional.ofNullable(row.selectFirst("td[data-title='Contracting authority']"))
                                .map(Element::text)
                                .orElseThrow(() -> new MissingHtmlElementException("Contracting authority element not found"))
                ))
                .map(row -> Optional.ofNullable(row.selectFirst("td[data-title='NEN system number']"))
                        .map(Element::text)
                        .orElseThrow(() -> new MissingHtmlElementException("NEN system number element not found"))
                )
                .collect(Collectors.toList());
    }
}
