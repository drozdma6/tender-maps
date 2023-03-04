package cz.cvut.fit.bap.parser.business.scrapper;

import cz.cvut.fit.bap.parser.business.fetcher.IFetcher;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
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

    public ContractorCompletedScrapper(IFetcher fetcher,
                                       ProcurementResultScrapper procurementResultScrapper,
                                       ContractorDetailScrapper contractorDetailScrapper){
        this.fetcher = fetcher;
        this.procurementResultScrapper = procurementResultScrapper;
        this.contractorDetailScrapper = contractorDetailScrapper;
    }

    public void scrapeContractorCompleted(String profile) throws IOException{
        Document document = fetcher.getContractorCompleted(profile);
        ContractorAuthority authority = contractorDetailScrapper.saveContractorAuthority(profile);
        Elements procurementRows = document.select(
                ".gov-table.gov-table--tablet-block.gov-sortable-table .gov-table__row");
        int i = 0;
        for (Element procurementRow : procurementRows){
            if (!procurementRow.select("[data-title=\"Status\"]").text().equals("Awarded")){
                continue;
            }
            String link = procurementRow.select("a").attr("href");

            procurementResultScrapper.scrapeProcurementResult(link, authority);
            i++;
            if (i > 2){
                break;
            }
        }
    }
}
