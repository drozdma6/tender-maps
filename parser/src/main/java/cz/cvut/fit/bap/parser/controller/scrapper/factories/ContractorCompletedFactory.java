package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorCompletedScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Contractor completed scrapper factory
 */
@Component
public class ContractorCompletedFactory extends AbstractScrapperFactory<ContractorCompletedScrapper>{

    protected ContractorCompletedFactory(AbstractFetcher fetcher){
        super(fetcher);
    }

    /**
     * Creates contractor completed scrapper instance
     *
     * @param document which is being scrapped
     * @return contractor completed scrapper instance
     */
    public ContractorCompletedScrapper create(Document document){
        return new ContractorCompletedScrapper(document);
    }
}
