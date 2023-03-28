package cz.cvut.fit.bap.parser.scrapper.fetcher;


import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class AbstractFetcher{
    public abstract Document getContractorDetail(String profile) throws IOException;

    public abstract Document getContractorCompleted(String profile, String contractorAuthorityName,
                                                    int iterationCounter) throws IOException;

    public abstract Document getProcurementResult(String procurement) throws IOException;

    public abstract Document getCompanyDetail(String uri) throws IOException;

    public abstract Document getProcurementDetail(String procurement) throws IOException;
}
