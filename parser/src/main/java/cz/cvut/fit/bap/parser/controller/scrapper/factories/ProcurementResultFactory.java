package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementResultScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Procurement result factory class
 */
@Component
public class ProcurementResultFactory extends AbstractScrapperFactory<ProcurementResultScrapper>{
    /**
     * Creates procurement result scrapper instance
     *
     * @param document which is being scrapped
     * @return ProcurementResultScrapper instance
     */
    @Override
    public ProcurementResultScrapper create(Document document){
        return new ProcurementResultScrapper(document);
    }
}
