package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.CurrencyExchanger;
import cz.cvut.fit.bap.parser.controller.dto.ContractDto;
import cz.cvut.fit.bap.parser.controller.dto.OfferDto;
import cz.cvut.fit.bap.parser.controller.dto.ProcurementDetailDto;
import cz.cvut.fit.bap.parser.controller.dto.ProcurementResultDto;
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
import kotlin.Pair;
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
                                 ProcurementListFactory procurementListFactory, OfferController offerController,
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
        CompletableFuture<ProcurementDetailDto> procurementDetailDtoFuture = fetcher.getProcurementDetail(systemNumber)
                .thenApply(procurementDetailFactory::create)
                .thenApply(this::getProcurementDetailDto);

        Document resultPageDoc = fetcher.getProcurementResult(systemNumber);
        ProcurementResultScrapper procurementResultScrapper = procurementResultFactory.create(resultPageDoc);
        ProcurementResultDto procurementResultDto = getProcurementResultDto(procurementResultScrapper);

        List<Pair<Company, BigDecimal>> participantList = getParticipants(procurementResultDto.participants());

        ProcurementDetailDto procurementDetailDto = procurementDetailDtoFuture.join();
        saveProcurementData(systemNumber, procurementResultDto, procurementDetailDto, participantList);
    }

    /**
     * Group the contracts by supplier name, then sum up all the contracts in each group and return the
     * list of contracts made by unique companies.
     *
     * @param contracts from procurement result page
     * @return list of contracts, with summed contract prices of same companies
     */
    public List<ContractDto> sumPricesAndFilterByCompanyName(List<ContractDto> contracts) {
        return new ArrayList<>(contracts.stream()
                .collect(Collectors.groupingBy(ContractDto::companyName))
                .values()
                .stream()
                .map(this::combineOffers)
                .toList());
    }

    /**
     * Exchanges all offers in foreign currencies to CZK. Offers do not have their date, so contractClose date is
     * used as the closest.
     *
     * @param offerDtos         offers to be exchanged (CZK offers are skipped)
     * @param contractCloseDate tender contract close date
     * @return List of exchanged offers to CZK
     */
    public List<OfferDto> exchangeCurrenciesToCZK(List<OfferDto> offerDtos, LocalDate contractCloseDate) {
        List<OfferDto> exchangedOffers = new ArrayList<>();
        for (OfferDto offerDto : offerDtos) {
            exchangedOffers.add(exchangeCurrencyToCzk(offerDto, contractCloseDate));
        }
        return exchangedOffers;
    }

    private void saveProcurementData(String systemNumber, ProcurementResultDto procurementResultDto,
                                     ProcurementDetailDto procurementDetailDto,
                                     List<Pair<Company, BigDecimal>> participants) {
        procurementResultDto.suppliers().forEach(contractDto -> {
            Company supplier = saveSupplier(contractDto);
            ContractingAuthority savedAuthority = contractingAuthorityController.save(procurementDetailDto.contractingAuthority());
            Procurement procurement = super.save(new Procurement(procurementDetailDto.procurementName(), supplier,
                    savedAuthority, contractDto.contractPrice(), procurementDetailDto.placeOfPerformance(),
                    procurementDetailDto.dateOfPublication(), systemNumber, contractDto.contractDate()));

            for (Pair<Company, BigDecimal> participant : participants) {
                Company savedCompany = companyController.save(participant.getFirst());
                Offer offer = new Offer(participant.getSecond(), procurement, savedCompany);
                offerController.save(offer);
            }
        });
    }

    private ProcurementDetailDto getProcurementDetailDto(ProcurementDetailScrapper procurementDetailScrapper) {
        ContractingAuthority contractingAuthority = contractingAuthorityController.getContractingAuthority(
                procurementDetailScrapper.getContractingAuthorityDto());
        return new ProcurementDetailDto(procurementDetailScrapper.getProcurementName(),
                procurementDetailScrapper.getProcurementPlaceOfPerformance(),
                procurementDetailScrapper.getProcurementDateOfPublication(), contractingAuthority);
    }

    private ProcurementResultDto getProcurementResultDto(ProcurementResultScrapper procurementResultScrapper) {
        List<OfferDto> offers = procurementResultScrapper.getParticipants();
        List<ContractDto> contracts = procurementResultScrapper.getSuppliers();
        return new ProcurementResultDto(exchangeCurrenciesToCZK(offers, contracts.get(0).contractDate()),
                sumPricesAndFilterByCompanyName(contracts));
    }

    private Company saveSupplier(ContractDto supplierInfo) {
        Company company = companyController.getCompany(supplierInfo.detailHref(), supplierInfo.companyName());
        return companyController.save(company);
    }

    /**
     * Gets participant companies, each participant runs in separate thread.
     *
     * @param participants scrapped data about participants from result page
     * @return List of companies and their offers
     */
    private List<Pair<Company, BigDecimal>> getParticipants(List<OfferDto> participants) {
        List<CompletableFuture<Pair<Company, BigDecimal>>> futures = participants.stream()
                .map(offerDto -> {
                    CompletableFuture<Company> participantFuture = companyController.getCompanyAsync(offerDto.detailHref(), offerDto.companyName());
                    return participantFuture.thenApply(company -> new Pair<>(company, offerDto.price()));
                })
                .toList();
        //wait for all to finish
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private ContractDto combineOffers(List<ContractDto> contracts) {
        BigDecimal totalPrice = computeTotalPrice(contracts);
        ContractDto firstOffer = contracts.get(0);
        return new ContractDto(
                totalPrice,
                firstOffer.detailHref(),
                firstOffer.companyName(),
                firstOffer.currency(),
                firstOffer.contractDate());
    }

    private BigDecimal computeTotalPrice(List<ContractDto> contracts) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ContractDto contractDto : contracts) {
            if (contractDto.contractPrice() != null) {
                if (contractDto.currency().equals(Currency.CZK)) {
                    totalPrice = totalPrice.add(contractDto.contractPrice());
                } else {
                    Optional<BigDecimal> priceInCZK = currencyExchanger.exchange(
                            contractDto.contractPrice(),
                            contractDto.currency(),
                            Currency.CZK,
                            contractDto.contractDate());
                    if (priceInCZK.isEmpty()) {
                        return null; //if exchange failed, store null value in database
                    }
                    totalPrice = totalPrice.add(priceInCZK.get());
                }
            }
        }
        return totalPrice;
    }

    private OfferDto exchangeCurrencyToCzk(OfferDto offerDto, LocalDate contractCloseDate) {
        if (offerDto.currency().equals(Currency.CZK)) {
            return offerDto;
        } else {
            BigDecimal convertedPrice = currencyExchanger.exchange(
                            offerDto.price(),
                            offerDto.currency(),
                            Currency.CZK,
                            contractCloseDate)
                    .orElse(null);
            return new OfferDto(convertedPrice, offerDto.detailHref(), offerDto.companyName(), Currency.CZK);
        }
    }
}