package cz.cvut.fit.bap.parser.controller.fetcher;


import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

/*
    Abstract class for methods returning jsoup documents of sites
 */
public abstract class AbstractFetcher{
    public abstract Document getAuthorityDetail(String href);

    public abstract Document getProcurementResult(String procurement);

    public abstract Document getSupplierDetail(String uri);

    @Async
    public abstract CompletableFuture<Document> getProcurementDetail(String procurement);

    public abstract Document getContractingAuthorityList(int page);

    public abstract Document getProcurementListPage(int page);

    @Async
    public abstract CompletableFuture<Document> getOfferDetailPage(String url);
}
