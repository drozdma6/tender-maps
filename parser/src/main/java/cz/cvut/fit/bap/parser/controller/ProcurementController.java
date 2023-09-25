package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
    Controller for procurements
 */
@Component
public class ProcurementController extends AbstractController<ProcurementService,Procurement,Long>{
    private final ProcurementResultFactory procurementResultFactory;
    private final ProcurementDetailFactory procurementDetailFactory;
    private final ProcurementListFactory procurementListFactory;
    private final OfferController offerController;
    private final CompanyController companyController;
    private final ContractingAuthorityController contractingAuthorityController;
    private final AbstractFetcher fetcher;

    public ProcurementController(ProcurementResultFactory procurementResultFactory,
                                 ProcurementDetailFactory procurementDetailFactory,
                                 ProcurementService procurementService,
                                 ProcurementListFactory procurementListFactory, OfferController offerController,
                                 CompanyController companyController,
                                 ContractingAuthorityController contractingAuthorityController, AbstractFetcher fetcher) {
        super(procurementService);
        this.procurementResultFactory = procurementResultFactory;
        this.procurementDetailFactory = procurementDetailFactory;
        this.procurementListFactory = procurementListFactory;
        this.offerController = offerController;
        this.companyController = companyController;
        this.contractingAuthorityController = contractingAuthorityController;
        this.fetcher = fetcher;
    }

    /**
     * Gets system numbers of procurements on given page
     *
     * @param page number of page which is supposed to be scrapped
     * @return list of system numbers
     */
    @Async
    public CompletableFuture<List<String>> getPageSystemNumbers(int page){
        Document document = fetcher.getProcurementListPage(page);
        ProcurementListScrapper procurementListScrapper = procurementListFactory.create(document);
        return CompletableFuture.completedFuture(procurementListScrapper.getProcurementSystemNumbers());
    }

    /**
     * Gets and saves all the neccessary data in order to store procurement with given systemNumber.
     * If there are multiple suppliers for single procurement creates and saves new Procurement for each supplier,
     * so that one procurement always has single supplier.
     *
     * @param systemNumber procurement system number
     */
    @Timed(value = "scrapper.procurement.save")
    @Transactional
    public void save(String systemNumber){
        if(service.existsBySystemNumber(systemNumber)){
            return;
        }
        //run procurement detail scrapping in separate thread
        CompletableFuture<ProcurementDetailDto> procurementDetailDtoFuture = fetcher.getProcurementDetail(systemNumber)
                .thenApply(procurementDetailFactory::create)
                .thenApply(this::getProcurementDetailDto);

        Document resultPageDoc = fetcher.getProcurementResult(systemNumber);
        ProcurementResultScrapper procurementResultScrapper = procurementResultFactory.create(resultPageDoc);
        ProcurementResultDto procurementResultDto = getProcurementResultDto(procurementResultScrapper);

        List<Pair<Company,BigDecimal>> participantList = getParticipants(procurementResultDto.participants());

        ProcurementDetailDto procurementDetailDto = procurementDetailDtoFuture.join();
        saveProcurementData(systemNumber, procurementResultDto, procurementDetailDto, participantList);
    }

    private void saveProcurementData(String systemNumber, ProcurementResultDto procurementResultDto,
                                     ProcurementDetailDto procurementDetailDto,
                                     List<Pair<Company,BigDecimal>> participants){
        procurementResultDto.suppliersMap().forEach((supplierName, supplierInfo) -> {
            Company supplier = saveSupplier(supplierInfo);
            ContractingAuthority savedAuthority = contractingAuthorityController.save(procurementDetailDto.contractingAuthority());
            Procurement procurement = super.save(new Procurement(procurementDetailDto.procurementName(), supplier,
                    savedAuthority, supplierInfo.contractPrice(), procurementDetailDto.placeOfPerformance(),
                    procurementDetailDto.dateOfPublication(), systemNumber));

            for(Pair<Company,BigDecimal> participant : participants){
                Company savedCompany = companyController.save(participant.getFirst());
                Offer offer = new Offer(participant.getSecond(), procurement, savedCompany);
                offerController.save(offer);
            }
        });
    }

    private ProcurementDetailDto getProcurementDetailDto(ProcurementDetailScrapper procurementDetailScrapper){
        ContractingAuthority contractingAuthority = contractingAuthorityController.getContractingAuthority(
                procurementDetailScrapper.getContractingAuthorityDto());
        return new ProcurementDetailDto(procurementDetailScrapper.getProcurementName(),
                procurementDetailScrapper.getProcurementPlaceOfPerformance(),
                procurementDetailScrapper.getProcurementDateOfPublication(), contractingAuthority);
    }

    private ProcurementResultDto getProcurementResultDto(ProcurementResultScrapper procurementResultScrapper){
        return new ProcurementResultDto(procurementResultScrapper.getParticipants(), procurementResultScrapper.getSupplierMap());
    }

    private Company saveSupplier(OfferDto supplierInfo){
        Company company = companyController.getCompany(supplierInfo.detailHref(), supplierInfo.companyName());
        return companyController.save(company);
    }

    /**
     * Gets participant companies, each participant runs in separate thread.
     *
     * @param participants scrapped data about participants from result page
     * @return List of companies and their offers
     */
    private List<Pair<Company,BigDecimal>> getParticipants(List<OfferDto> participants){
        List<CompletableFuture<Pair<Company,BigDecimal>>> futures = participants.stream()
                .map(offerDto -> {
                    CompletableFuture<Company> participantFuture = companyController.getCompanyAsync(offerDto.detailHref(), offerDto.companyName());
                    return participantFuture.thenApply(company -> new Pair<>(company, offerDto.contractPrice()));
                })
                .toList();
        //wait for all to finish
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}