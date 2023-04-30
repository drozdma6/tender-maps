package cz.cvut.fit.bap.parser.controller.fetcher;


import org.jsoup.nodes.Document;


public abstract class AbstractFetcher{
    public abstract Document getContractorDetail(String href);

    public abstract Document getContractorCompleted(String profile, Integer page);

    public abstract Document getProcurementResult(String procurement);

    public abstract Document getCompanyDetail(String uri);

    public abstract Document getProcurementDetail(String procurement);

    public abstract Document getContractorAuthorityList(Integer page);
}
