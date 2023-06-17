package cz.cvut.fit.bap.parser.controller.fetcher;


import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;


public abstract class AbstractFetcher{
    public abstract Document getContractorDetail(String href);

    public abstract Document getProcurementResult(String procurement);

    public abstract Document getCompanyDetail(String uri);

    @Async
    public abstract CompletableFuture<Document> getProcurementDetail(String procurement);

    public abstract Document getContractorAuthorityList(int page);

    public abstract Document getProcurementListPage(int page);
}
