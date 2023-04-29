package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import org.jsoup.nodes.Document;
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
     * @param document which is being scrapped
     * @return ProcurementDetailScrapper instance
     */
    @Override
    public ProcurementDetailScrapper create(Document document){
        return new ProcurementDetailScrapper(document);
    }
}
