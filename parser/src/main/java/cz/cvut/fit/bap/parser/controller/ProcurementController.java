package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.dto.OfferDto;
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
    @Timed(value = "scrapper.procurement.save")
    public boolean saveProcurement(ContractorAuthority authority, String systemNumber){
        if(service.existsBySystemNumber(systemNumber)){
            return false;
        }
        //run procurement detail scrapping in separate thread
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
                    supplierInfo.contractPrice(), procurementDetailDto.placeOfPerformance(),
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

    private Company saveSupplier(OfferDto supplierInfo){
        CompletableFuture<Company> company = companyController.getCompany(supplierInfo.detailHref(), supplierInfo.companyName());
        return companyController.saveCompany(company.join());
    }

    private List<Pair<Company,BigDecimal>> saveParticipants(List<OfferDto> participants){
        List<Pair<CompletableFuture<Company>,BigDecimal>> companyFutures = new ArrayList<>();
        participants.forEach(offerDto -> {
            //run scrapping in separate thread
            CompletableFuture<Company> participantFuture = companyController.getCompany(offerDto.detailHref(), offerDto.companyName());
            companyFutures.add(new Pair<>(participantFuture, offerDto.contractPrice()));
        });
        //Wait for all futures to finish
        return finishFutures(companyFutures)
                .stream()
                .map(pair -> new Pair<>(companyController.saveCompany(pair.getFirst()), pair.getSecond())) //save each participant
                .toList();
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
                .toList();
    }
}