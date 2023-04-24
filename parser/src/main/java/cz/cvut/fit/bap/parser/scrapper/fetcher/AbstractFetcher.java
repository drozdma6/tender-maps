package cz.cvut.fit.bap.parser.scrapper.fetcher;


import org.jsoup.nodes.Document;


public abstract class AbstractFetcher{
    public abstract Document getContractorDetail(String profile);

    public abstract Document getContractorCompleted(String profile, String contractorAuthorityName,
                                                    Integer iterationCounter);

    public abstract Document getProcurementResult(String procurement);

    public abstract Document getCompanyDetail(String uri);

    public abstract Document getProcurementDetail(String procurement);
}
