package cz.cvut.fit.bap.parser.business.fetcher;


import org.jsoup.nodes.Document;

import java.io.IOException;

public interface IFetcher{
    Document getContractorDetail(String profile) throws IOException;

    Document getContractorCompleted(String profile) throws IOException;

    Document getProcurementResult(String procurement) throws IOException;

    Document getCompanyDetail(String uri) throws IOException;

    Document getProcurementDetail(String procurement) throws IOException;
}
