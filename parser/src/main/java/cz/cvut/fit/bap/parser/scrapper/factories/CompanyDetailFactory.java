package cz.cvut.fit.bap.parser.scrapper.factories;

import cz.cvut.fit.bap.parser.scrapper.CompanyDetailScrapper;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.springframework.stereotype.Component;

/*
    Company detail scrapper factory
 */
@Component
public class CompanyDetailFactory extends AbstractScrapperFactory<CompanyDetailScrapper>{

    protected CompanyDetailFactory(AbstractFetcher fetcher){
        super(fetcher);
    }

    /**
     * Creates CompanyDetailScrapper instance
     *
     * @param url of company detail page
     * @return CompanyDetailScrapper instance
     */
    public CompanyDetailScrapper create(String url){
        return new CompanyDetailScrapper(fetcher, url);
    }
}