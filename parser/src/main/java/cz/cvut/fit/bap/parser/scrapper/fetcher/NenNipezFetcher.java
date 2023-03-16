package cz.cvut.fit.bap.parser.scrapper.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of IFetcher, used for fetching "<a href="https://nen.nipez.cz">nen.nipez</a>"
 */
@Component
public class NenNipezFetcher implements IFetcher{
    private final String baseUrl = "https://nen.nipez.cz";

    /**
     * Fetches contractor detail site.
     *
     * @param profile of contractor
     * @return Document containing contractor detail site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getContractorDetail(String profile) throws IOException{
        final String url = baseUrl + "/en/profily-zadavatelu-platne/detail-profilu/" + profile;
        return Jsoup.connect(url).get();
    }

    /**
     * Fetches document containing list of completed procurements by provided authority profile.
     * Filters only awarded procurements.
     *
     * @param profile                 of contracting authority
     * @param contractorAuthorityName name of contracting authority
     * @return document containing contractor completed site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getContractorCompleted(String profile, String contractorAuthorityName)
            throws IOException{
        final String url = baseUrl + "/en/profily-zadavatelu-platne/detail-profilu/" + profile +
                           "/uzavrene-zakazky/p:puvz:stavZP=zadana&zadavatelNazev=" +
                           UriUtils.encode(contractorAuthorityName, StandardCharsets.UTF_8);
        return Jsoup.connect(url).get();
    }


    /**
     * Fetches procurement result site.
     *
     * @param systemNumber of procurement
     * @return Document containing procurement result site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getProcurementResult(String systemNumber) throws IOException{
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url =
                baseUrl + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen + "/vysledek";
        return Jsoup.connect(url).get();
    }


    /**
     * Fetches company detail site.
     *
     * @param detailUrl addition to base url for desired company
     * @return Document containing company detail site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getCompanyDetail(String detailUrl) throws IOException{
        return Jsoup.connect(baseUrl + detailUrl).get();
    }


    /**
     * Fetches procurement detail site.
     *
     * @param systemNumber procurement
     * @return Document containing procurement detail site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getProcurementDetail(String systemNumber) throws IOException{
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = baseUrl + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen;
        return Jsoup.connect(url).get();
    }
}
