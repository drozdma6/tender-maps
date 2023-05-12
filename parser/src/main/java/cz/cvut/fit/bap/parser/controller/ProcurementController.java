package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.dto.CompanyDto;
import cz.cvut.fit.bap.parser.controller.dto.ProcurementDetailDto;
import cz.cvut.fit.bap.parser.controller.dto.ProcurementResultDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementResultFactory;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Procurement;
import io.micrometer.core.annotation.Timed;
import kotlin.Pair;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/*
    Controller handling communication with procurement service and required scrappers to get procurement
 */
@Component
public class ProcurementController extends AbstractController<ProcurementService>{
    private final ProcurementResultFactory procurementResultFactory;
    private final ProcurementDetailFactory procurementDetailFactory;
    private final OfferController offerController;
    private final CompanyController companyController;
    private final AbstractFetcher fetcher;

    public ProcurementController(ProcurementResultFactory procurementResultFactory,
                                 ProcurementDetailFactory procurementDetailFactory,
                                 ProcurementService procurementService,
                                 OfferController offerController,
                                 CompanyController companyController,
                                 AbstractFetcher fetcher){
        super(procurementService);
        this.procurementResultFactory = procurementResultFactory;
        this.procurementDetailFactory = procurementDetailFactory;
        this.offerController = offerController;
        this.companyController = companyController;
        this.fetcher = fetcher;
    }

    /**
     * Saves procurements. If there are multiple suppliers for single procurement creates and saves
     * new Procurement for each supplier, so that one procurement always has single supplier.
     *
     * @param authority    contracting authority of procurement
     * @param systemNumber procurement system number
     * @return false if procurement is already in database, true otherwise
     */
    public boolean saveProcurement(ContractorAuthority authority, String systemNumber) throws ExecutionException, InterruptedException{
        if(service.existsBySystemNumber(systemNumber)){
            return false;
        }
        CompletableFuture<ProcurementDetailDto> procurementDetailDtoFuture = fetcher.getProcurementDetail(systemNumber)
                .thenApply(procurementDetailFactory::create)
                .thenApply(this::getProcurementDetailDto);

        Document resultPageDoc = fetcher.getProcurementResult(systemNumber);
        ProcurementResultScrapper procurementResultScrapper = procurementResultFactory.create(resultPageDoc);
        ProcurementResultDto procurementResultDto = getProcurementResultDto(procurementResultScrapper);

        List<Pair<Company,BigDecimal>> participantList = saveParticipants(procurementResultDto.participants());

        ProcurementDetailDto procurementDetailDto = procurementDetailDtoFuture.join();
        saveProcurementData(authority, systemNumber, procurementResultDto, procurementDetailDto, participantList);
        return true;
    }

    private void saveProcurementData(ContractorAuthority authority, String systemNumber,
                                     ProcurementResultDto procurementResultDto,
                                     ProcurementDetailDto procurementDetailDto,
                                     List<Pair<Company,BigDecimal>> participants){
        procurementResultDto.suppliersMap().forEach((supplierName, supplierInfo) -> {
            Company supplier = saveSupplier(supplierInfo);

            Procurement procurement = service.create(new Procurement(procurementDetailDto.procurementName(), supplier, authority,
                    supplierInfo.getContractPrice(), procurementDetailDto.placeOfPerformance(),
                    procurementDetailDto.dateOfPublication(), systemNumber));

            for(Pair<Company,BigDecimal> participant : participants){
                offerController.saveOffer(participant.getSecond(), procurement, participant.getFirst());
            }
        });
    }

    private ProcurementDetailDto getProcurementDetailDto(ProcurementDetailScrapper procurementDetailScrapper){
        return new ProcurementDetailDto(procurementDetailScrapper.getProcurementName(),
                procurementDetailScrapper.getProcurementPlaceOfPerformance(),
                procurementDetailScrapper.getProcurementDateOfPublication());
    }

    private ProcurementResultDto getProcurementResultDto(ProcurementResultScrapper procurementResultScrapper){
        return new ProcurementResultDto(procurementResultScrapper.getParticipants(), procurementResultScrapper.getSupplierMap());
    }

    private Company saveSupplier(CompanyDto supplierInfo){
        Company supplier = companyController.getCompany(supplierInfo.getDetailHref(), supplierInfo.getCompanyName());
        return companyController.saveCompany(supplier);
    }

    private List<Pair<Company,BigDecimal>> saveParticipants(List<CompanyDto> participants){
        //run scrapping of each participant in separate thread
        List<Pair<CompletableFuture<Company>,BigDecimal>> companyFutures = new ArrayList<>();
        participants.forEach(companyDto -> {
            CompletableFuture<Company> participantFuture = companyController.getCompanyAsync(companyDto.getDetailHref(), companyDto.getCompanyName());
            companyFutures.add(new Pair<>(participantFuture, companyDto.getContractPrice()));
        });
        //Wait for all participants to finish
        List<Pair<Company,BigDecimal>> participantList = finishFutures(companyFutures);

        participantList.forEach(pair -> companyController.saveCompany(pair.getFirst()));
        return participantList;
    }

    private List<Pair<Company,BigDecimal>> finishFutures(List<Pair<CompletableFuture<Company>,BigDecimal>> futures){
        CompletableFuture<?>[] cfs = futures.stream()
                .map(Pair::getFirst)
                .toArray(CompletableFuture[]::new);
        // Wait for all CompletableFuture objects to complete
        CompletableFuture.allOf(cfs).join();

        // Collect the results and return as a List<Pair<Company, BigDecimal>>
        return futures.stream()
                .map(pair -> new Pair<>(pair.getFirst().join(), pair.getSecond()))
                .collect(Collectors.toList());
    }
}