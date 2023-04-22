package cz.cvut.fit.bap.parser.scrapper.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of abstract fetcher, used for fetching "<a href="https://nen.nipez.cz">nen.nipez</a>"
 */
@Component
public class NenNipezFetcher extends AbstractFetcher{
    private final String baseUrl = "https://nen.nipez.cz";

    @Value("${procurement.pages.per.fetch:5}") //default value is 5
    private int procurementPagesPerFetch;  //number of fetched pages per document

    @Value("${procurement.first.date.of.publication:2022-01-01}") //default value is 2022-01-01
    private String firstDateOfPublication; //since when should procurements be scrapped

    /**
     * Fetches contractor detail site.
     *
     * @param profile of contractor
     * @return Document containing contractor detail site
     */
    @Override
    public Document getContractorDetail(String profile){
        final String url = baseUrl + "/en/profily-zadavatelu-platne/detail-profilu/" +
                           UriUtils.encode(profile, StandardCharsets.UTF_8);
        return getDocumentWithRetry(url);
    }

    /**
     * Fetches document containing list of completed procurements by provided authority profile.
     * Filters only awarded procurements created by given contracting authority and created later
     * than firstDateOfPublication.
     *
     * @param profile                 of contracting authority
     * @param contractorAuthorityName name of contracting authority
     * @param pagingIteration         used to determine which pages are supposed to get fetched
     * @return document containing contractor completed site
     */
    @Override
    public Document getContractorCompleted(String profile, String contractorAuthorityName,
                                           int pagingIteration){
        int rangeEnd = pagingIteration * procurementPagesPerFetch;
        int rangeStart = rangeEnd - (procurementPagesPerFetch - 1);
        String pageRange = rangeStart + "-" + rangeEnd;
        final String url = baseUrl + "/en/profily-zadavatelu-platne/detail-profilu/" +
                           UriUtils.encode(profile, StandardCharsets.UTF_8) +
                           "/uzavrene-zakazky/p:puvz:stavZP=zadana&page=" + pageRange +
                           "&zadavatelNazev=" +
                           UriUtils.encode(contractorAuthorityName, StandardCharsets.UTF_8) +
                           "&datumPrvniUver=" +
                           UriUtils.encode(firstDateOfPublication, StandardCharsets.UTF_8) + ",";

        return getDocumentWithRetry(url);
    }

    /**
     * Fetches procurement result site.
     *
     * @param systemNumber of procurement
     * @return Document containing procurement result site
     */
    @Override
    public Document getProcurementResult(String systemNumber){
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = baseUrl + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen +
                           "/vysledek/p:vys:page=1-10;uca:page=1-10"; //show all participants and suppliers without paging
        return getDocumentWithRetry(url);
    }


    /**
     * Fetches company detail site
     *
     * @param detailUrl addition to base url for desired company
     * @return Document containing company detail site
     */
    @Override
    public Document getCompanyDetail(String detailUrl){
        //removes redundant information from url to increase performance
        String pattern = "/p:[^/]*/"; //matches /p:vys:page=1-10;uca:page=1-10
        String url = baseUrl + detailUrl.replaceFirst(pattern, "/");
        return getDocumentWithRetry(url);
    }


    /**
     * Fetches procurement detail site.
     *
     * @param systemNumber procurement
     * @return Document containing procurement detail site
     */
    @Override
    public Document getProcurementDetail(String systemNumber){
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = baseUrl + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen;
        return getDocumentWithRetry(url);
    }

    /**
     * Fetches document from url with exponential backoff
     *
     * @param url which is supposed to be fetched
     * @return document
     */
    private Document getDocumentWithRetry(String url){
        int backoffSeconds = 1; //initial backoff time
        int maxRetries = 8;

        for (int i = 0; i < maxRetries; i++){
            try{
                return Jsoup.connect(url).get();
            } catch (IOException e){
                try{
                    TimeUnit.SECONDS.sleep(backoffSeconds);
                    backoffSeconds = backoffSeconds * 2;
                } catch (InterruptedException ex){
                    throw new RuntimeException(ex);
                }
            }
        }
        throw new RuntimeException("Failed to retrieve document after " + maxRetries + " tries");
    }
}
