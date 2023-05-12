package cz.cvut.fit.bap.parser.controller.fetcher;

import io.micrometer.core.annotation.Timed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of abstract fetcher, used for fetching "<a href="https://nen.nipez.cz">nen.nipez</a>"
 */
@Component
public class NenNipezFetcher extends AbstractFetcher{
    private final String baseUrl = "https://nen.nipez.cz";

    private static final Logger LOGGER = LoggerFactory.getLogger(NenNipezFetcher.class);

    /**
     * Fetches contractor detail site.
     *
     * @param href of contractor authority
     * @return Document containing contractor detail site
     */
    @Override
    public Document getContractorDetail(String href){
        final String url = baseUrl + "/en" + href;
        return getDocumentWithRetry(url);
    }

    /**
     * Fetches document containing list of completed procurements by provided authority profile.
     *
     * @param href of contracting authority
     * @param page which page is supposed to get fetched
     * @return document containing contractor completed site
     */
    @Override
    public Document getContractorCompleted(String href, Integer page){
        final String url = baseUrl + "/en" + href +
                "/uzavrene-zakazky/p:puvz:stavZP=zadana&page=" + page.toString();
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
    public CompletableFuture<Document> getProcurementDetail(String systemNumber){
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = baseUrl + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen;
        return CompletableFuture.completedFuture(getDocumentWithRetry(url));
    }

    /**
     * Fetches contractor authority list
     *
     * @param page which is supposed to get fetched
     * @return document containing contractor authorities
     */
    @Override
    public Document getContractorAuthorityList(Integer page){
        String url = baseUrl + "/profily-zadavatelu-platne/p:pzp:page=" + page;
        return getDocumentWithRetry(url);
    }


    /**
     * Fetches document from url with exponential backoff
     *
     * @param url which is supposed to be fetched
     * @return document
     */
    @Timed(value = "scrapper.nen.nipez.fetch")
    private Document getDocumentWithRetry(String url){
        int backoffSeconds = 1; //initial backoff time
        int maxRetries = 5;

        long startTime, endTime, duration;
        startTime = System.currentTimeMillis();

        for(int i = 0; i < maxRetries; i++){
            try{
                Document doc = Jsoup.connect(url).get();
                endTime = System.currentTimeMillis();
                duration = endTime - startTime;
                LOGGER.debug("Fetch in " + duration + " ms with " + i + " retries." + url);
                return doc;
            }catch(IOException e){
                try{
                    TimeUnit.SECONDS.sleep(backoffSeconds);
                    backoffSeconds = backoffSeconds * 2;
                }catch(InterruptedException ex){
                    throw new RuntimeException(ex);
                }
            }
        }
        LOGGER.debug("Failed to fetch url: " + url);
        throw new FailedFetchException("Failed to fetch url: " + url);
    }
}