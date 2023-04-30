package cz.cvut.fit.bap.parser.controller.scrapper;

import kotlin.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/*
    Class for scrapping contractor list page.

    @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne">Contractor list page</a>
 */
public class ContractorListScrapper extends AbstractScrapper{
    public ContractorListScrapper(Document document){
        super(document);
    }

    /**
     * Gets information about contracting authorities from document
     *
     * @return list of pairs where first element is link to detail information about authority
     * and second is its profile name
     */
    public List<Pair<String,String>> getAuthoritiesHrefs(){
        List<Pair<String,String>> authorities = new ArrayList<>();
        Elements rows = document.select(".gov-table__row");
        for(Element row : rows){
            String href = row.select("a").attr("href");
            String profile = row.select("td.gov-table__cell:nth-of-type(4)").text();
            authorities.add(new Pair<>(href, profile));
        }
        return authorities;
    }
}
