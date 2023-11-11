package cz.cvut.fit.bap.parser.controller.fetcher;

import cz.cvut.fit.bap.parser.controller.scrapper.*;
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
     * Gets scrapper for authority detail page
     *
     * @param href to contracting authority detail page
     * @return authorityDetailScrapper
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public AuthorityDetailScrapper getAuthorityDetailScrapper(String href) {
        final String url = BASE_URL + href;
        return new AuthorityDetailScrapper(getDocumentWithRetry(url));
    }

    /**
     * Gets scrapper for procurement result page.
     *
     * @param systemNumber of procurement
     * @return procurementResultScrapper
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public ProcurementResultScrapper getProcurementResultScrapper(String systemNumber) {
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = BASE_URL + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen +
                "/vysledek/p:vys:page=1-50;uca:page=1-50"; //show all participants and suppliers without paging
        return new ProcurementResultScrapper(getDocumentWithRetry(url));
    }


    /**
     * Gets scrapper for supplier detail page
     *
     * @param detailUrl addition to base url for desired supplier
     * @return supplierDetailScrapper
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public SupplierDetailScrapper getSupplierDetailScrapper(String detailUrl) {
        String pattern = "/p:[^/]*/"; //matches /p:vys:page=1-10;uca:page=1-10
        String url = BASE_URL + detailUrl.replaceFirst(pattern, "/");
        return new SupplierDetailScrapper(getDocumentWithRetry(url));
    }


    /**
     * Gets procurementDetailScrapper for  procurement detail site.
     *
     * @param systemNumber of procurement
     * @return future of procurementDetailScrapper
     */
    @Override
    @Async
    @Timed(value = "scrapper.nen.nipez.fetch")
    public CompletableFuture<ProcurementDetailScrapper> getProcurementDetailScrapper(String systemNumber) {
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = BASE_URL + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen;
        return CompletableFuture.completedFuture(new ProcurementDetailScrapper(getDocumentWithRetry(url)));
    }

    /**
     * Gets procurementListScrapper for procurement list page
     *
     * @param pageNumber which is supposed to get fetched
     * @return procurementListScrapper
     */
    @Override
    @Timed(value = "scrapper.nen.nipez.fetch")
    public ProcurementListScrapper getProcurementListScrapper(int pageNumber) {
        String url = BASE_URL + "/en/verejne-zakazky/p:vz:stavZP=zadana,plneni&page=" + pageNumber;
        return new ProcurementListScrapper(getDocumentWithRetry(url));
    }

    /**
     * Gets offerDetailScrapper for offer detail page
     *
     * @param url addition to base url for desired offer
     * @return offerDetailScrapper
     */
    @Override
    @Async
    @Timed(value = "scrapper.nen.nipez.fetch")
    public CompletableFuture<OfferDetailScrapper> getOfferDetailScrapper(String url) {
        String pattern = "/p:[^/]*/"; //matches /p:vys:page=1-10;uca:page=1-10
        String detailUrl = BASE_URL + url.replaceFirst(pattern, "/");
        return CompletableFuture.completedFuture(new OfferDetailScrapper(getDocumentWithRetry(detailUrl)));
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