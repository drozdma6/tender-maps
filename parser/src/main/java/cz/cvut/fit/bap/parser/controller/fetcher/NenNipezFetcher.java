package cz.cvut.fit.bap.parser.controller.fetcher;

import io.micrometer.core.annotation.Timed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of abstract fetcher, used for fetching "<a href="https://nen.nipez.cz">nen.nipez</a>"
 */
@Component
public class NenNipezFetcher extends AbstractFetcher{
    private static final String BASE_URL = "https://nen.nipez.cz";
    private static final Logger LOGGER = LoggerFactory.getLogger(NenNipezFetcher.class);

    /**
     * Fetches contracting detail site.
     *
     * @param href of contracting authority
     * @return Document containing contracting detail site
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public Document getAuthorityDetail(String href) {
        final String url = BASE_URL + href;
        return getDocumentWithRetry(url);
    }

    /**
     * Fetches procurement result site.
     *
     * @param systemNumber of procurement
     * @return Document containing procurement result site
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public Document getProcurementResult(String systemNumber){
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = BASE_URL + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen +
                "/vysledek/p:vys:page=1-50;uca:page=1-50"; //show all participants and suppliers without paging
        return getDocumentWithRetry(url);
    }


    /**
     * Fetches supplier detail site
     *
     * @param detailUrl addition to base url for desired company
     * @return Document containing supplier detail site
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public Document getSupplierDetail(String detailUrl) {
        String pattern = "/p:[^/]*/"; //matches /p:vys:page=1-10;uca:page=1-10
        String url = BASE_URL + detailUrl.replaceFirst(pattern, "/");
        return getDocumentWithRetry(url);
    }


    /**
     * Fetches procurement detail site.
     *
     * @param systemNumber procurement
     * @return Document containing procurement detail site
     */
    @Override
    @Async
    @Timed(value = "scrapper.nen.nipez.fetch")
    public CompletableFuture<Document> getProcurementDetail(String systemNumber){
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = BASE_URL + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen;
        return CompletableFuture.completedFuture(getDocumentWithRetry(url));
    }

    /**
     * Fetches contracting authority list
     *
     * @param page which is supposed to get fetched
     * @return document containing contracting authorities
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public Document getContractingAuthorityList(int page) {
        String url = BASE_URL + "/profily-zadavatelu-platne/p:pzp:page=" + page;
        return getDocumentWithRetry(url);
    }

    /**
     * Fetches procurement list page
     *
     * @param page which is supposed to get fetched
     * @return document containing wanted page of procurements
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public Document getProcurementListPage(int page){
        String url = BASE_URL + "/en/verejne-zakazky/p:vz:stavZP=zadana,plneni&page=" + page;
        return getDocumentWithRetry(url);
    }

    @Override
    @Async
    @Timed(value = "scrapper.nen.nipez.fetch")
    public CompletableFuture<Document> getOfferDetailPage(String url) {
        String pattern = "/p:[^/]*/"; //matches /p:vys:page=1-10;uca:page=1-10
        String detailUrl = BASE_URL + url.replaceFirst(pattern, "/");
        return CompletableFuture.completedFuture(getDocumentWithRetry(detailUrl));
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

        long startTime, endTime, duration;
        startTime = System.currentTimeMillis();

        for(int i = 0; i < maxRetries; i++){
            try{
                Document doc = Jsoup.connect(url).get();
                endTime = System.currentTimeMillis();
                duration = endTime - startTime;
                LOGGER.debug("Fetch in {} ms with {} retries for {}", duration, i, url);
                return doc;
            }catch(IOException e){
                try{
                    TimeUnit.SECONDS.sleep(backoffSeconds);
                    backoffSeconds = backoffSeconds * 2;
                }catch(InterruptedException ex){
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ex);
                }
            }
        }
        LOGGER.debug("Failed to fetch url: {}", url);
        throw new FailedFetchException("Failed to fetch url: " + url);
    }
}