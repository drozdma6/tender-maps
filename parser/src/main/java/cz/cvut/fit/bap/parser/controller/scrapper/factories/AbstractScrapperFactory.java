package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import org.jsoup.nodes.Document;

/*
    Abstract factory for scrappers
 */
public abstract class AbstractScrapperFactory<S>{
    public abstract S create(Document document);
}