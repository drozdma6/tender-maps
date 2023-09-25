package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.AuthorityDetailScrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/*
    Contracting authority detail scrapper factory
 */
@Component
public class AuthorityDetailFactory extends AbstractScrapperFactory<AuthorityDetailScrapper> {
    /**
     * Creates ContractingDetailScrapper instance
     *
     * @param document which is being scrapped
     * @return ContractorDetailScrapper instance
     */
    @Override
    public AuthorityDetailScrapper create(Document document) {
        return new AuthorityDetailScrapper(document);
    }
}
