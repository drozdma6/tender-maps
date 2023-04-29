package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.ContractorDetailScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Contractor detail scrapper factory
 */
@Component
public class ContractorDetailFactory extends AbstractScrapperFactory<ContractorDetailScrapper>{
    /**
     * Creates ContractorDetailScrapper instance
     *
     * @param document which is being scrapped
     * @return ContractorDetailScrapper instance
     */
    @Override
    public ContractorDetailScrapper create(Document document){
        return new ContractorDetailScrapper(document);
    }
}
