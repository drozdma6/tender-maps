package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.OfferDetailScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class OfferDetailFactory extends AbstractScrapperFactory<OfferDetailScrapper> {
    @Override
    public OfferDetailScrapper create(Document document) {
        return new OfferDetailScrapper(document);
    }
}
