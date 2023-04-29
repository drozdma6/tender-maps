package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import org.jsoup.nodes.Document;

/*
    Abstract factory for scrappers
 */
public abstract class AbstractScrapperFactory<S>{
    protected final AbstractFetcher fetcher;

    protected AbstractScrapperFactory(AbstractFetcher fetcher){
        this.fetcher = fetcher;
    }

    public abstract S create(Document document);
}