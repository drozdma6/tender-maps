package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.jsoup.nodes.Document;


public abstract class AbstractScrapper{
    protected final AbstractFetcher fetcher;
    protected Document document;

    public AbstractScrapper(AbstractFetcher fetcher){
        this.fetcher = fetcher;
    }
}
