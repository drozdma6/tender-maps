package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.builder.OfferBuilder;
import cz.cvut.fit.bap.parser.controller.builder.ProcurementBuilder;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.CurrencyExchanger;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.FailedExchangeException;
import cz.cvut.fit.bap.parser.controller.data.*;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementListScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.Procurement;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
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
    private final OfferController offerController;
    private final CompanyController companyController;
    private final ContractingAuthorityController contractingAuthorityController;
    private final AbstractFetcher fetcher;
    private final CurrencyExchanger currencyExchanger;

    public ProcurementController(ProcurementService procurementService,
                                 OfferController offerController,
                                 CompanyController companyController,
                                 ContractingAuthorityController contractingAuthorityController,
                                 AbstractFetcher fetcher,
                                 CurrencyExchanger currencyExchanger) {
        super(procurementService);
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
        ProcurementListScrapper procurementListScrapper = fetcher.getProcurementListScrapper(page);
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
                fetcher.getProcurementDetailScrapper(systemNumber).thenApply(ProcurementDetailScrapper::getPageData);
        CompletableFuture<ContractingAuthority> contractingAuthorityDtoFuture =
                procurementDetailDtoFuture.thenApply(procurementDetailPageData ->
                        contractingAuthorityController.getContractingAuthority(
                                procurementDetailPageData.contractingAuthorityName(),
                                procurementDetailPageData.contractingAuthorityUrl())
                );

        ProcurementResultScrapper procurementResultScrapper = fetcher.getProcurementResultScrapper(systemNumber);
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
        contracts.forEach(contract -> {
            SupplierDetailPageData supplierDetailPageData = companyController.getSupplierDetailPageData(contract.detailHref());
            Company savedSupplier = saveSupplier(contract.companyName(), supplierDetailPageData);
            ContractingAuthority savedAuthority = contractingAuthorityController.save(contractingAuthority);
            Procurement procurement = new ProcurementBuilder(procurementDetailPageData, contract, savedSupplier,
                    savedAuthority, supplierDetailPageData.isAssociationOfSuppliers(), systemNumber)
                    .build();
            Procurement savedProcurement = super.save(procurement);

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
            if (!cd.hasEmptyPrice()) {
                try {
                    summedPriceData = sumContracts(summedPriceData, cd);
                } catch (FailedExchangeException e) {
                    e.printStackTrace();
                    return new ContractData(null, null, null, null, summedPriceData.detailHref(),
                            summedPriceData.companyName(), summedPriceData.currency(), summedPriceData.contractDate());
                }
            }
        }
        return summedPriceData;
    }

    private ContractData sumContracts(ContractData originalContract, ContractData newContract) {
        if (newContract.currency() == Currency.CZK) {
            return originalContract.sumContracts(newContract);
        }
        ContractData exchangedContract = exchangeContractData(newContract);
        return originalContract.sumContracts(exchangedContract);
    }

    private ContractData exchangeContractData(ContractData contractData) {
        List<BigDecimal> prices = List.of(contractData.price(),
                contractData.priceVAT(),
                contractData.priceWithAmend(),
                contractData.priceWithAmendVAT());
        List<BigDecimal> pricesInCZK = currencyExchanger.exchange(prices, contractData.currency(), Currency.CZK, contractData.contractDate());
        return new ContractData(pricesInCZK.get(0),
                pricesInCZK.get(1),
                pricesInCZK.get(2),
                pricesInCZK.get(3),
                contractData.detailHref(),
                contractData.companyName(),
                Currency.CZK,
                contractData.contractDate());
    }
}