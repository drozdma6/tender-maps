package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.controller.builder.OfferBuilder;
import cz.cvut.fit.bap.parser.controller.data.OfferData;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.OfferDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.OfferDetailFactory;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.Offer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
    Controller for offers
 */
@Component
public class OfferController extends AbstractController<OfferService, Offer, Long> {
    private final AbstractFetcher fetcher;
    private final OfferDetailFactory offerDetailFactory;
    private final CompanyController companyController;

    public OfferController(OfferService offerService, AbstractFetcher fetcher, OfferDetailFactory offerDetailFactory, CompanyController companyController) {
        super(offerService);
        this.fetcher = fetcher;
        this.offerDetailFactory = offerDetailFactory;
        this.companyController = companyController;
    }

    /**
     * Gets offers by scrapping each participant in separate future and then waiting for all futures to finish.
     *
     * @param participantRows data scrapped from procurement result page
     * @return list of offers containing all data about offers
     */
    public List<OfferBuilder> getOffers(List<OfferData> participantRows) {
        List<CompletableFuture<OfferBuilder>> offerBuilderFutures = new ArrayList<>();
        for (OfferData offerDataRow : participantRows) {
            CompletableFuture<OfferBuilder> offerBuilderFut =
                    fetcher.getOfferDetailPage(offerDataRow.detailHref())
                            .thenApply(offerDetailFactory::create)
                            .thenApply(OfferDetailScrapper::getPageData)
                            .thenApply(offerDetailPageData -> {
                                Company company = companyController.buildCompany(
                                        offerDataRow.companyName(),
                                        offerDetailPageData.addressData(),
                                        offerDetailPageData.organisationId(),
                                        offerDetailPageData.VATIdNumber());
                                return new OfferBuilder(offerDataRow, offerDetailPageData).company(company);
                            });
            offerBuilderFutures.add(offerBuilderFut);
        }
        return offerBuilderFutures.stream().map(CompletableFuture::join).toList();
    }
}
