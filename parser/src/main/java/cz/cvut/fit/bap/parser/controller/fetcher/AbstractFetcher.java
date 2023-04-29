package cz.cvut.fit.bap.parser.controller.fetcher;


import org.jsoup.nodes.Document;


public abstract class AbstractFetcher{
    public abstract Document getContractorDetail(String profile);

    public abstract Document getContractorCompleted(String profile, Integer iterationCounter);

    public abstract Document getProcurementResult(String procurement);

    public abstract Document getCompanyDetail(String uri);

    public abstract Document getProcurementDetail(String procurement);
}
