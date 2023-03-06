package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ContractorCompletedScrapper{
    private final IFetcher fetcher;
    private final ProcurementResultScrapper procurementResultScrapper;
    private final ContractorDetailScrapper contractorDetailScrapper;
    private final ProcurementService procurementService;

    public ContractorCompletedScrapper(IFetcher fetcher,
                                       ProcurementResultScrapper procurementResultScrapper,
                                       ContractorDetailScrapper contractorDetailScrapper,
                                       ProcurementService procurementService){
        this.fetcher = fetcher;
        this.procurementResultScrapper = procurementResultScrapper;
        this.contractorDetailScrapper = contractorDetailScrapper;
        this.procurementService = procurementService;
    }

    public void scrape(String profile) throws IOException{
        Document document = fetcher.getContractorCompleted(profile);
        ContractorAuthority authority = contractorDetailScrapper.scrape(profile);
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
