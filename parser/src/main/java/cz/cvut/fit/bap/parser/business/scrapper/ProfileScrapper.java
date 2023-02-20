package cz.cvut.fit.bap.parser.business.scrapper;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.business.fetcher.IFetcher;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class ProfileScrapper{
    private final IFetcher fetcher;
    private final ContractorAuthorityService service;
    private final DetailScrapper detailScrapper;

    public ProfileScrapper(IFetcher fetcher, ContractorAuthorityService service,
                           DetailScrapper detailScrapper){
        this.fetcher = fetcher;
        this.service = service;
        this.detailScrapper = detailScrapper;
    }

    private ContractorAuthority saveContractorAuthority(String profile, Document document){
        String contractorName = Objects.requireNonNull(
                document.select("td.gov-table__cell:nth-of-type(5)").first()).text();

        return service.create(new ContractorAuthority(contractorName, profile));
    }

    public void scrapeProfile(String profile) throws IOException{
        Document document = fetcher.getProfile(profile);
        ContractorAuthority authority = saveContractorAuthority(profile, document);
        Elements procurementRows = document.select(
                ".gov-table.gov-table--tablet-block.gov-sortable-table .gov-table__row");
        int i = 0;
        for (Element procurementRow : procurementRows){
            if (!procurementRow.select("[data-title=\"Status\"]").text().equals("Awarded")){
                continue;
            }
            String link = procurementRow.select("a").attr("href");

            detailScrapper.scrapeDetail(link, authority);
            i++;
            if (i > 2){
                break;
            }
        }
    }
}
