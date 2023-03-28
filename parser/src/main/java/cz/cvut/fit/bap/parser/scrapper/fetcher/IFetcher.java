package cz.cvut.fit.bap.parser.scrapper.fetcher;


import org.jsoup.nodes.Document;

import java.io.IOException;

public interface IFetcher{
    Document getContractorDetail(String profile) throws IOException;

    Document getContractorCompleted(String profile, String contractorAuthorityName,
                                    int iterationCounter) throws IOException;

    Document getProcurementResult(String procurement) throws IOException;

    Document getCompanyDetail(String uri) throws IOException;

    Document getProcurementDetail(String procurement) throws IOException;
}
