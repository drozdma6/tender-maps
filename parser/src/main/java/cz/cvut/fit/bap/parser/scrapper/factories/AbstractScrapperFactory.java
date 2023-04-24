package cz.cvut.fit.bap.parser.scrapper.factories;

import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;

/*
    Abstract factory for scrappers
 */
public abstract class AbstractScrapperFactory<S>{
    protected final AbstractFetcher fetcher;

    protected AbstractScrapperFactory(AbstractFetcher fetcher){
        this.fetcher = fetcher;
    }

    public abstract S create(String str);
}