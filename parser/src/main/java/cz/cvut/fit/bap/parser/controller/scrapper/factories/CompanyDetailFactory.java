package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.SupplierDetailScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Company detail scrapper factory
 */
@Component
public class CompanyDetailFactory extends AbstractScrapperFactory<SupplierDetailScrapper> {
    /**
     * Creates SupplierDetailScrapper instance
     *
     * @param document which is being scrapped
     * @return SupplierDetailScrapper instance
     */
    public SupplierDetailScrapper create(Document document) {
        return new SupplierDetailScrapper(document);
    }
}