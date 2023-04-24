package cz.cvut.fit.bap.parser.scrapper.factories;

import cz.cvut.fit.bap.parser.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.springframework.stereotype.Component;

/*
    Procurement result factory class
 */
@Component
public class ProcurementResultFactory extends AbstractScrapperFactory<ProcurementResultScrapper>{

    protected ProcurementResultFactory(AbstractFetcher fetcher){
        super(fetcher);
    }

    /**
     * Creates procurement result scrapper instance
     *
     * @param systemNumber of procurement which scrapper is supposed to be created
     * @return ProcurementResultScrapper instance
     */
    public ProcurementResultScrapper create(String systemNumber){
        return new ProcurementResultScrapper(fetcher, systemNumber);
    }
}
