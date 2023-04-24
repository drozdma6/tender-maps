package cz.cvut.fit.bap.parser.scrapper.factories;

import cz.cvut.fit.bap.parser.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.springframework.stereotype.Component;

/*
    Contractor detail scrapper factory
 */
@Component
public class ContractorDetailFactory extends AbstractScrapperFactory<ContractorDetailScrapper>{

    protected ContractorDetailFactory(AbstractFetcher fetcher){
        super(fetcher);
    }

    /**
     * Creates ContractorDetailScrapper instance
     *
     * @param profile of contractor
     * @return ContractorDetailScrapper instance
     */
    public ContractorDetailScrapper create(String profile){
        return new ContractorDetailScrapper(fetcher, profile);
    }
}
