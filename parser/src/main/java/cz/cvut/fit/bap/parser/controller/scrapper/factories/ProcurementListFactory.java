package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementListScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Procurement list factory class
 */
@Component
public class ProcurementListFactory extends AbstractScrapperFactory<ProcurementListScrapper>{
    /**
     * Creates procurement list scrapper instance
     *
     * @param document which is being scrapped
     * @return ProcurementListScrapper instance
     */
    @Override
    public ProcurementListScrapper create(Document document){
        return new ProcurementListScrapper(document);
    }
}
