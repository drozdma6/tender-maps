package cz.cvut.fit.bap.parser.scrapper.factories;

import cz.cvut.fit.bap.parser.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.springframework.stereotype.Component;


/*
    Procurement detail factory class
 */
@Component
public class ProcurementDetailFactory extends AbstractScrapperFactory<ProcurementDetailScrapper>{

    protected ProcurementDetailFactory(AbstractFetcher fetcher){
        super(fetcher);
    }

    /**
     * Creates procurement detail scrapper instance
     *
     * @param systemNumber of procurement which scrapper is supposed to be created
     * @return ProcurementDetailScrapper instance
     */
    public ProcurementDetailScrapper create(String systemNumber){
        return new ProcurementDetailScrapper(fetcher, systemNumber);
    }
}
