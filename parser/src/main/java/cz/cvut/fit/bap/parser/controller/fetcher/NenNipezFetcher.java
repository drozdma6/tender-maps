package cz.cvut.fit.bap.parser.controller.fetcher;

import cz.cvut.fit.bap.parser.controller.fetcher.utility.ExponentialBackoffFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.*;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of abstract fetcher, used for fetching "<a href="https://nen.nipez.cz">nen.nipez</a>"
 */
@Component
public class NenNipezFetcher extends AbstractFetcher {
    private static final String BASE_URL = "https://nen.nipez.cz";
    private final ExponentialBackoffFetcher exponentialBackoffFetcher;

    public NenNipezFetcher(ExponentialBackoffFetcher exponentialBackoffFetcher) {
        this.exponentialBackoffFetcher = exponentialBackoffFetcher;
    }

    /**
     * Gets scrapper for authority detail page
     *
     * @param href to contracting authority detail page
     * @return authorityDetailScrapper
     */
    @Override
    public AuthorityDetailScrapper getAuthorityDetailScrapper(String href) {
        final String url = BASE_URL + href;
        Document doc = exponentialBackoffFetcher.getDocumentWithRetry(url);
        return new AuthorityDetailScrapper(doc);
    }

    /**
     * Gets scrapper for procurement result page.
     *
     * @param systemNumber of procurement
     * @return procurementResultScrapper
     */
    @Override
    public ProcurementResultScrapper getProcurementResultScrapper(String systemNumber) {
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = BASE_URL + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen +
                "/vysledek/p:vys:page=1-50;uca:page=1-50"; //show all participants and suppliers without paging
        Document doc = exponentialBackoffFetcher.getDocumentWithRetry(url);
        return new ProcurementResultScrapper(doc);
    }


    /**
     * Gets scrapper for supplier detail page
     *
     * @param detailUrl addition to base url for desired supplier
     * @return supplierDetailScrapper
     */
    @Override
    public SupplierDetailScrapper getSupplierDetailScrapper(String detailUrl) {
        String pattern = "/p:[^/]*/"; //matches /p:vys:page=1-10;uca:page=1-10
        String url = BASE_URL + detailUrl.replaceFirst(pattern, "/");
        Document doc = exponentialBackoffFetcher.getDocumentWithRetry(url);
        return new SupplierDetailScrapper(doc);
    }


    /**
     * Gets procurementDetailScrapper for  procurement detail site.
     *
     * @param systemNumber of procurement
     * @return future of procurementDetailScrapper
     */
    @Override
    @Async
    public CompletableFuture<ProcurementDetailScrapper> getProcurementDetailScrapper(String systemNumber) {
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = BASE_URL + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen;
        Document doc = exponentialBackoffFetcher.getDocumentWithRetry(url);
        return CompletableFuture.completedFuture(new ProcurementDetailScrapper(doc));
    }

    /**
     * Gets procurementListScrapper for procurement list page
     *
     * @param pageNumber which is supposed to get fetched
     * @return procurementListScrapper
     */
    @Override
    public ProcurementListScrapper getProcurementListScrapper(int pageNumber) {
        String url = BASE_URL + "/en/verejne-zakazky/p:vz:stavZP=zadana,plneni&page=" + pageNumber;
        Document doc = exponentialBackoffFetcher.getDocumentWithRetry(url);
        return new ProcurementListScrapper(doc);
    }

    /**
     * Gets offerDetailScrapper for offer detail page
     *
     * @param url addition to base url for desired offer
     * @return offerDetailScrapper
     */
    @Override
    @Async
    public CompletableFuture<OfferDetailScrapper> getOfferDetailScrapper(String url) {
        String pattern = "/p:[^/]*/"; //matches /p:vys:page=1-10;uca:page=1-10
        String detailUrl = BASE_URL + url.replaceFirst(pattern, "/");
        Document doc = exponentialBackoffFetcher.getDocumentWithRetry(detailUrl);
        return CompletableFuture.completedFuture(new OfferDetailScrapper(doc));
    }
}