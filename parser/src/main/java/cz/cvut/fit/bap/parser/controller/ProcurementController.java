package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.builder.OfferBuilder;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.CurrencyExchanger;
import cz.cvut.fit.bap.parser.controller.data.*;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
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
import java.util.ArrayList;
import java.util.List;
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
        ProcurementListPageData procurementListPageData = procurementListScrapper.getPageData();
        return CompletableFuture.completedFuture(procurementListPageData.systemNumbers());
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
        ProcurementResultPageData procurementResultPageData = procurementResultScrapper.getPageData();
        List<ContractData> summedContracts = groupByCompanyAndSum(procurementResultPageData.suppliers());

        List<OfferBuilder> offerBuilders = offerController.getOffers(procurementResultPageData.participants(),
                summedContracts.get(0).contractDate());

        ProcurementDetailPageData procurementDetailPageData = procurementDetailDtoFuture.join();
        ContractingAuthority contractingAuthority = contractingAuthorityDtoFuture.join();

        saveProcurementData(systemNumber, summedContracts, procurementDetailPageData, offerBuilders, contractingAuthority);
    }

    /**
     * Group the contracts by supplier name, then sum up all the contracts in each group and return the
     * list of contracts made by unique companies.
     *
     * @param contracts from procurement result page
     * @return list of contracts, with summed contract prices of same companies
     */
    public List<ContractData> groupByCompanyAndSum(List<ContractData> contracts) {
        return new ArrayList<>(contracts.stream()
                .collect(Collectors.groupingBy(ContractData::companyName))
                .values()
                .stream()
                .map(this::combineContracts)
                .toList());
    }

    private void saveProcurementData(String systemNumber, List<ContractData> contracts,
                                     ProcurementDetailPageData procurementDetailPageData,
                                     List<OfferBuilder> offerBuilders, ContractingAuthority contractingAuthority) {
        contracts.forEach(contractData -> {
            SupplierDetailPageData supplierDetailPageData = companyController.getSupplierDetailPageData(contractData.detailHref());
            Company savedSupplier = saveSupplier(contractData.companyName(), supplierDetailPageData);
            ContractingAuthority savedAuthority = contractingAuthorityController.save(contractingAuthority);
            Procurement savedProcurement = saveProcurement(procurementDetailPageData, savedAuthority, contractData,
                    supplierDetailPageData.isAssociationOfSuppliers(), systemNumber, savedSupplier);

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
                                        Boolean isAssociationOfSuppliers,
                                        String systemNumber,
                                        Company supplier) {
        Procurement procurement = new Procurement(
                procurementDetailPageData.procurementName(),
                supplier,
                isAssociationOfSuppliers,
                contractingAuthority,
                contractData.price(),
                contractData.priceVAT(),
                contractData.priceWithAmend(),
                contractData.priceWithAmendVAT(),
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

    private Company saveSupplier(String companyName, SupplierDetailPageData supplierDetailPageData) {
        Company supplier = companyController.buildCompany(
                companyName,
                supplierDetailPageData.addressData(),
                supplierDetailPageData.organisationId(),
                supplierDetailPageData.VATIdNumber());
        return companyController.save(supplier);
    }

    private ContractData combineContracts(List<ContractData> contracts) {
        if (contracts.isEmpty()) {
            // this should not occur
            throw new MissingHtmlElementException("List of contracts can not be empty.");
        }
        ContractData firstContract = contracts.get(0);
        ContractData summedPriceData = new ContractData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                firstContract.detailHref(), firstContract.companyName(), firstContract.currency(), firstContract.contractDate());
        for (ContractData cd : contracts) {
            if (!hasEmptyPrice(cd)) {
                if (cd.currency().equals(Currency.CZK)) {
                    summedPriceData = sumContractDataPrices(summedPriceData, cd.price(), cd.priceVAT(),
                            cd.priceWithAmend(), cd.priceWithAmendVAT());
                } else {
                    List<BigDecimal> prices = List.of(cd.price(), cd.priceVAT(), cd.priceWithAmend(), cd.priceWithAmendVAT());
                    List<BigDecimal> pricesInCZK = currencyExchanger.exchange(prices, cd.currency(), Currency.CZK, cd.contractDate());
                    if (pricesInCZK.isEmpty()) {
                        //if exchange failed, store null value in database
                        return new ContractData(null, null, null, null,
                                cd.detailHref(), cd.companyName(), cd.currency(), cd.contractDate());
                    }
                    summedPriceData = sumContractDataPrices(summedPriceData, pricesInCZK.get(0), pricesInCZK.get(1),
                            pricesInCZK.get(2), pricesInCZK.get(3));
                }
            }
        }
        return summedPriceData;
    }

    private ContractData sumContractDataPrices(ContractData cd1, BigDecimal price, BigDecimal priceVAT, BigDecimal priceAmend, BigDecimal priceAmendVAT) {
        return new ContractData(cd1.price().add(price),
                cd1.priceVAT().add(priceVAT),
                cd1.priceWithAmend().add(priceAmend),
                cd1.priceWithAmendVAT().add(priceAmendVAT),
                cd1.detailHref(),
                cd1.companyName(),
                cd1.currency(),
                cd1.contractDate()
        );
    }

    private boolean hasEmptyPrice(ContractData contractData) {
        return contractData.price() == null || contractData.priceVAT() == null
                || contractData.priceWithAmend() == null || contractData.priceWithAmendVAT() == null;
    }
}