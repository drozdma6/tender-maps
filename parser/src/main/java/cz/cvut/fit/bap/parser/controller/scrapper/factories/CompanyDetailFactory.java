package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.CompanyDetailScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Company detail scrapper factory
 */
@Component
public class CompanyDetailFactory extends AbstractScrapperFactory<CompanyDetailScrapper>{
    /**
     * Creates CompanyDetailScrapper instance
     *
     * @param document which is being scrapped
     * @return CompanyDetailScrapper instance
     */
    public CompanyDetailScrapper create(Document document){
        return new CompanyDetailScrapper(document);
    }
}