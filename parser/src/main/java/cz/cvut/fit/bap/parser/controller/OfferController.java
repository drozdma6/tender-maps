package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.controller.builder.OfferBuilder;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.CurrencyExchanger;
import cz.cvut.fit.bap.parser.controller.data.OfferData;
import cz.cvut.fit.bap.parser.controller.data.OfferDetailPageData;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.Offer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
    Controller for offers
 */
@Component
public class OfferController extends AbstractController<OfferService, Offer, Long> {
    private final AbstractFetcher fetcher;
    private final CompanyController companyController;
    private final CurrencyExchanger currencyExchanger;

    public OfferController(OfferService offerService,
                           AbstractFetcher fetcher,
                           CompanyController companyController,
                           CurrencyExchanger currencyExchanger) {
        super(offerService);
        this.fetcher = fetcher;
        this.companyController = companyController;
        this.currencyExchanger = currencyExchanger;
    }

    /**
     * Gets offers by scrapping each participant in separate future and then waiting for all futures to finish.
     *
     * @param participantRows data scrapped from procurement result page
     * @return list of offers containing all data about offers
     */
    public List<OfferBuilder> getOffers(List<OfferData> participantRows, LocalDate contractCloseDate) {
        List<CompletableFuture<OfferBuilder>> offerBuilderFutures = new ArrayList<>();
        for (OfferData offerDataRow : participantRows) {
            CompletableFuture<OfferBuilder> offerBuilderFut =
                    fetcher.getOfferDetailScrapper(offerDataRow.detailHref())
                            .thenApply(offerDetailScrapper -> {
                                OfferDetailPageData offerDetailPageData = offerDetailScrapper.getPageData();
                                Company company = companyController.buildCompany(
                                        offerDataRow.companyName(),
                                        offerDetailPageData.addressData(),
                                        offerDetailPageData.organisationId(),
                                        offerDetailPageData.VATIdNumber());
                                OfferData offerData = exchangeCurrenciesToCZK(offerDataRow, contractCloseDate);
                                return new OfferBuilder(offerData, offerDetailPageData).company(company);
                            });
            offerBuilderFutures.add(offerBuilderFut);
        }
        return offerBuilderFutures.stream().map(CompletableFuture::join).toList();
    }

    private OfferData exchangeCurrenciesToCZK(OfferData offerData, LocalDate contractCloseDate) {
        if (offerData.currency().equals(Currency.CZK)) {
            return offerData;
        }
        List<BigDecimal> convertedPrice = currencyExchanger.exchange(
                List.of(offerData.price(), offerData.priceVAT()),
                offerData.currency(),
                Currency.CZK,
                contractCloseDate);
        if (convertedPrice.size() != 2) {
            return new OfferData(null, null, offerData.detailHref(), offerData.companyName(), Currency.CZK);
        }
        return new OfferData(convertedPrice.get(0),
                convertedPrice.get(1),
                offerData.detailHref(),
                offerData.companyName(),
                Currency.CZK);
    }
}
