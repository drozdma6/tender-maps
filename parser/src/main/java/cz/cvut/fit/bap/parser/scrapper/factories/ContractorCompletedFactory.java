package cz.cvut.fit.bap.parser.scrapper.factories;

import cz.cvut.fit.bap.parser.scrapper.ContractorCompletedScrapper;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
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
     * Forbids the usage of create with parameter since ContractorCompleted doesn't need it
     *
     * @param str parameter
     * @return exception
     */
    public ContractorCompletedScrapper create(String str){
        throw new UnsupportedOperationException("Contractor completed factory with str parameter");
    }

    /**
     * Creates contractor completed scrapper instance
     *
     * @return contractor completed scrapper instance
     */
    public ContractorCompletedScrapper create(){
        return new ContractorCompletedScrapper(fetcher);
    }
}
