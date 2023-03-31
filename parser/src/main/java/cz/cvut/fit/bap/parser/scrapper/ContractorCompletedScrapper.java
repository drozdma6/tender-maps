package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Class for scrapping authority's completed list of procurements
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR/uzavrene-zakazky">contractor authority completed page</a>
 */
@Component
public class ContractorCompletedScrapper extends AbstractScrapper{
    private final ProcurementResultScrapper procurementResultScrapper;
    private final ProcurementService procurementService;

    @Value("${procurement.scrape.all:false}") //default value is false
    private Boolean scrapeAll;

    public ContractorCompletedScrapper(AbstractFetcher fetcher,
                                       ProcurementResultScrapper procurementResultScrapper,
                                       ProcurementService procurementService){
        super(fetcher);
        this.procurementResultScrapper = procurementResultScrapper;
        this.procurementService = procurementService;
    }

    /**
     * Scrapes contractor authority completed procurements page. Ends scrapping when first already saved
     * procurement is found.
     *
     * @param authority which profile is supposed to get scrapped
     * @throws IOException if wrong profile was given
     */
    public void scrape(ContractorAuthority authority) throws IOException{
        int iterationCounter = 1; //counts iterations, used for loading certain pages in fetcher

        document = fetcher.getContractorCompleted(authority.getProfile(), authority.getName(),
                                                  iterationCounter);
        //end scrapping once document with .table-empty-message is found - solves paging
        while (document.select(".table-empty-message").isEmpty()){
            Elements procurementRows = document.select(
                    ".gov-table.gov-table--tablet-block.gov-sortable-table .gov-table__row");
            for (Element procurementRow : procurementRows){
                String procurementSystemNumber = procurementRow.select(
                        "[data-title=\"NEN system number\"]").text();
                if (procurementService.existsBySystemNumber(procurementSystemNumber)){
                    //continue scrapping next procurement
                    if (scrapeAll){
                        continue;
                    }
                    //stop scrapping this authority when first already saved procurement is found
                    return;
                }
                try{
                    //skip procurements with insufficient information
                    procurementResultScrapper.scrape(authority, procurementSystemNumber);
                } catch (MissingHtmlElementException e){
                    continue;
                }
            }
            iterationCounter++;
            document = fetcher.getContractorCompleted(authority.getProfile(), authority.getName(),
                                                      iterationCounter);
        }
    }
}
