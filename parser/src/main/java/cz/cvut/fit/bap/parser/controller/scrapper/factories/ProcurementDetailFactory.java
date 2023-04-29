package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;


/*
    Procurement detail factory class
 */
@Component
public class ProcurementDetailFactory extends AbstractScrapperFactory<ProcurementDetailScrapper>{
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
