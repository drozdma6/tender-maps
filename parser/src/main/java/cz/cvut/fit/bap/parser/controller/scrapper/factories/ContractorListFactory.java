package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.ContractorListScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Contractor list page scrapper factory
 */
@Component
public class ContractorListFactory extends AbstractScrapperFactory<ContractorListScrapper>{
    /**
     * Creates a ContractorListScrapper instance
     *
     * @param document which is supposed to be scrapped
     * @return ContractorListScrapper instance
     */
    @Override
    public ContractorListScrapper create(Document document){
        return new ContractorListScrapper(document);
    }
}
