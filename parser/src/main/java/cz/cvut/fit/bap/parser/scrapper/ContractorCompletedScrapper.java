package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    public ContractorCompletedScrapper(IFetcher fetcher,
                                       ProcurementResultScrapper procurementResultScrapper,
                                       ProcurementService procurementService){
        super(fetcher);
        this.procurementResultScrapper = procurementResultScrapper;
        this.procurementService = procurementService;
    }

    /**
     * Scrapes contractor authority completed procurements page
     *
     * @param authority which profile is supposed to get scrapped
     * @throws IOException if wrong profile was given
     */
    public void scrape(ContractorAuthority authority) throws IOException{
        document = fetcher.getContractorCompleted(authority.getProfile());
        Elements procurementRows = document.select(
                ".gov-table.gov-table--tablet-block.gov-sortable-table .gov-table__row");
        for (Element procurementRow : procurementRows){
            String procurementSystemNumber = procurementRow.select(
                    "[data-title=\"NEN system number\"]").text();
            //stop scrapping when first already saved procurement is found
            if (procurementService.existsBySystemNumber(procurementSystemNumber)){
                break;
            }
            //scrape only awarded procurements
            if (!procurementRow.select("[data-title=\"Status\"]").text().equals("Awarded")){
                continue;
            }
            String link = procurementRow.select("a").attr("href");
            try{
                //skip procurements with insufficient information
                procurementResultScrapper.scrape(link, authority, procurementSystemNumber);
            } catch (MissingHtmlElementException e){
                continue;
            }
        }
    }
}
