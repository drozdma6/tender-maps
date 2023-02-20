package cz.cvut.fit.bap.parser.business.fetcher;


import org.jsoup.nodes.Document;

import java.io.IOException;

public interface IFetcher{
    Document getProfile(String profile) throws IOException;

    Document getDetail(String procurement) throws IOException;
}
