package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Document;


public abstract class AbstractScrapper{
    protected final IFetcher fetcher;
    protected Document document;

    public AbstractScrapper(IFetcher fetcher){
        this.fetcher = fetcher;
    }
}
