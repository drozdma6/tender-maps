package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.builder.OfferBuilder;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.CurrencyExchanger;
import cz.cvut.fit.bap.parser.controller.data.*;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementListScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementListFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementResultFactory;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.Procurement;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/*
    Controller for procurements
 */
@Component
public class ProcurementController extends AbstractController<ProcurementService, Procurement, Long> {
    private final ProcurementResultFactory procurementResultFactory;
    private final ProcurementDetailFactory procurementDetailFactory;
    private final ProcurementListFactory procurementListFactory;
    private final OfferController offerController;
    private final CompanyController companyController;
    private final ContractingAuthorityController contractingAuthorityController;
    private final AbstractFetcher fetcher;
    private final CurrencyExchanger currencyExchanger;

    public ProcurementController(ProcurementResultFactory procurementResultFactory,
                                 ProcurementDetailFactory procurementDetailFactory,
                                 ProcurementService procurementService,
                                 ProcurementListFactory procurementListFactory,
                                 OfferController offerController,
                                 CompanyController companyController,
                                 ContractingAuthorityController contractingAuthorityController,
                                 AbstractFetcher fetcher,
                                 CurrencyExchanger currencyExchanger) {
        super(procurementService);
        this.procurementResultFactory = procurementResultFactory;
        this.procurementDetailFactory = procurementDetailFactory;
        this.procurementListFactory = procurementListFactory;
        this.offerController = offerController;
        this.companyController = companyController;
        this.contractingAuthorityController = contractingAuthorityController;
        this.fetcher = fetcher;
        this.currencyExchanger = currencyExchanger;
    }

    /**
     * Gets system numbers of procurements on given page
     *
     * @param page number of page which is supposed to be scrapped
     * @return list of system numbers
     */
    @Async
    public CompletableFuture<List<String>> getPageSystemNumbers(int page) {
        Document document = fetcher.getProcurementListPage(page);
        ProcurementListScrapper procurementListScrapper = procurementListFactory.create(document);
        return CompletableFuture.completedFuture(procurementListScrapper.getProcurementSystemNumbers());
    }

    /**
     * Gets and saves all the necessary data in order to store procurement with given systemNumber.
     * If there are multiple suppliers for single procurement creates and saves new Procurement for each supplier,
     * so that one procurement always has single supplier.
     *
     * @param systemNumber procurement system number
     */
    @Timed(value = "scrapper.procurement.save")
    @Transactional
    public void save(String systemNumber) {
        if (service.existsBySystemNumber(systemNumber)) {
            return;
        }
        //run procurement detail scrapping in separate thread
        CompletableFuture<ProcurementDetailPageData> procurementDetailDtoFuture =
                fetcher.getProcurementDetail(systemNumber)
                        .thenApply(procurementDetailFactory::create)
                        .thenApply(ProcurementDetailScrapper::getPageData);
        CompletableFuture<ContractingAuthority> contractingAuthorityDtoFuture =
                procurementDetailDtoFuture.thenApply(procurementDetailPageData ->
                        contractingAuthorityController.getContractingAuthority(
                                procurementDetailPageData.contractingAuthorityName(),
                                procurementDetailPageData.contractingAuthorityUrl())
                );

        Document resultPageDoc = fetcher.getProcurementResult(systemNumber);
        ProcurementResultScrapper procurementResultScrapper = procurementResultFactory.create(resultPageDoc);
        ProcurementResultPageData procurementResultPageData = getProcurementResultDto(procurementResultScrapper);

        List<OfferBuilder> offerBuilders = offerController.getOffers(procurementResultPageData.participants());


        ProcurementDetailPageData procurementDetailPageData = procurementDetailDtoFuture.join();
        ContractingAuthority contractingAuthority = contractingAuthorityDtoFuture.join();

        saveProcurementData(systemNumber, procurementResultPageData, procurementDetailPageData, offerBuilders, contractingAuthority);
    }

    /**
     * Group the contracts by supplier name, then sum up all the contracts in each group and return the
     * list of contracts made by unique companies.
     *
     * @param contracts from procurement result page
     * @return list of contracts, with summed contract prices of same companies
     */
    public List<ContractData> sumPricesAndFilterByCompanyName(List<ContractData> contracts) {
        return new ArrayList<>(contracts.stream()
                .collect(Collectors.groupingBy(ContractData::companyName))
                .values()
                .stream()
                .map(this::combineOffers)
                .toList());
    }

    /**
     * Exchanges all offers in foreign currencies to CZK. Offers do not have their date, so contractClose date is
     * used as the closest.
     *
     * @param offerDataList     offers to be exchanged (CZK offers are skipped)
     * @param contractCloseDate tender contract close date
     * @return List of exchanged offers to CZK
     */
    public List<OfferData> exchangeCurrenciesToCZK(List<OfferData> offerDataList, LocalDate contractCloseDate) {
        List<OfferData> exchangedOffers = new ArrayList<>();
        for (OfferData offerData : offerDataList) {
            exchangedOffers.add(exchangeCurrencyToCzk(offerData, contractCloseDate));
        }
        return exchangedOffers;
    }

    private void saveProcurementData(String systemNumber, ProcurementResultPageData procurementResultPageData,
                                     ProcurementDetailPageData procurementDetailPageData,
                                     List<OfferBuilder> offerBuilders,
                                     ContractingAuthority contractingAuthority) {
        procurementResultPageData.suppliers().forEach(contractData -> {
            SupplierDetailPageData supplierDetailPageData = companyController.getSupplierDetailPageData(contractData.detailHref());
            Company savedSupplier = saveSupplier(contractData.companyName(), supplierDetailPageData);
            ContractingAuthority savedAuthority = contractingAuthorityController.save(contractingAuthority);
            Procurement savedProcurement = saveProcurement(
                    procurementDetailPageData,
                    savedAuthority,
                    contractData,
                    supplierDetailPageData,
                    systemNumber,
                    savedSupplier);

            for (OfferBuilder offerBuilder : offerBuilders) {
                Company savedParticipant = companyController.save(offerBuilder.getCompany());
                Offer offerWithSavedData = offerBuilder
                        .company(savedParticipant)
                        .procurement(savedProcurement)
                        .build();
                offerController.save(offerWithSavedData);
            }
        });
    }

    private Procurement saveProcurement(ProcurementDetailPageData procurementDetailPageData,
                                         ContractingAuthority contractingAuthority,
                                         ContractData contractData,
                                        SupplierDetailPageData supplierDetailPageData,
                                         String systemNumber,
                                         Company supplier) {
        Procurement procurement = new Procurement(
                procurementDetailPageData.procurementName(),
                supplier,
                supplierDetailPageData.isAssociationOfSuppliers(),
                contractingAuthority,
                contractData.contractPrice(),
                supplierDetailPageData.contractPriceVAT(),
                supplierDetailPageData.contractPriceWithAmendments(),
                supplierDetailPageData.contractPriceWithAmendmentsVAT(),
                procurementDetailPageData.placeOfPerformance(),
                procurementDetailPageData.dateOfPublication(),
                systemNumber,
                contractData.contractDate(),
                procurementDetailPageData.type(),
                procurementDetailPageData.typeOfProcedure(),
                procurementDetailPageData.publicContractRegime(),
                procurementDetailPageData.bidsSubmissionDeadline(),
                procurementDetailPageData.codeFromNipezCodeList(),
                procurementDetailPageData.nameFromNipezCodeList()
        );
        return super.save(procurement);
    }

    private ProcurementResultPageData getProcurementResultDto(ProcurementResultScrapper procurementResultScrapper) {
        List<OfferData> offers = procurementResultScrapper.getParticipants();
        List<ContractData> contracts = procurementResultScrapper.getSuppliers();
        return new ProcurementResultPageData(exchangeCurrenciesToCZK(offers, contracts.get(0).contractDate()),
                sumPricesAndFilterByCompanyName(contracts));
    }

    private Company saveSupplier(String companyName, SupplierDetailPageData supplierDetailPageData) {
        Company supplier = companyController.buildCompany(
                companyName,
                supplierDetailPageData.addressData(),
                supplierDetailPageData.organisationId(),
                supplierDetailPageData.VATIdNumber());
        return companyController.save(supplier);
    }

    private ContractData combineOffers(List<ContractData> contracts) {
        BigDecimal totalPrice = computeTotalPrice(contracts);
        ContractData firstOffer = contracts.get(0);
        return new ContractData(
                totalPrice,
                firstOffer.detailHref(),
                firstOffer.companyName(),
                firstOffer.currency(),
                firstOffer.contractDate());
    }

    private BigDecimal computeTotalPrice(List<ContractData> contracts) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ContractData contractData : contracts) {
            if (contractData.contractPrice() != null) {
                if (contractData.currency().equals(Currency.CZK)) {
                    totalPrice = totalPrice.add(contractData.contractPrice());
                } else {
                    Optional<BigDecimal> priceInCZK = currencyExchanger.exchange(
                            contractData.contractPrice(),
                            contractData.currency(),
                            Currency.CZK,
                            contractData.contractDate());
                    if (priceInCZK.isEmpty()) {
                        return null; //if exchange failed, store null value in database
                    }
                    totalPrice = totalPrice.add(priceInCZK.get());
                }
            }
        }
        return totalPrice;
    }

    private OfferData exchangeCurrencyToCzk(OfferData offerData, LocalDate contractCloseDate) {
        if (offerData.currency().equals(Currency.CZK)) {
            return offerData;
        } else {
            BigDecimal convertedPrice = currencyExchanger.exchange(
                            offerData.price(),
                            offerData.currency(),
                            Currency.CZK,
                            contractCloseDate)
                    .orElse(null);
            return new OfferData(convertedPrice, offerData.detailHref(), offerData.companyName(), Currency.CZK);
        }
    }
}