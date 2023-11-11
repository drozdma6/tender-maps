package cz.cvut.fit.bap.parser.controller.fetcher;


import cz.cvut.fit.bap.parser.controller.scrapper.*;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

/*
    Abstract fetcher returning scrappers for each scrapped page
 */
public abstract class AbstractFetcher{
    public abstract AuthorityDetailScrapper getAuthorityDetailScrapper(String href);

    public abstract ProcurementResultScrapper getProcurementResultScrapper(String procurement);

    public abstract SupplierDetailScrapper getSupplierDetailScrapper(String uri);

    @Async
    public abstract CompletableFuture<ProcurementDetailScrapper> getProcurementDetailScrapper(String procurement);

    public abstract ProcurementListScrapper getProcurementListScrapper(int page);

    @Async
    public abstract CompletableFuture<OfferDetailScrapper> getOfferDetailScrapper(String url);
}
