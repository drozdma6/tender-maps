package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementResultFactory;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Procurement;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    /*
        A container class for procurement information, used as a dto between methods.
     */
    private static class ProcurementInfo{
        String systemNumber;
        ContractorAuthority contractorAuthority;
        String procurementName;
        String procurementPlaceOfPerformance;
        LocalDate procurementDateOfPublication;
        ArrayList<ProcurementResultScrapper.CompanyInfo> participants;
        HashMap<String,ProcurementResultScrapper.CompanyInfo> suppliersMap;
    }

    public ProcurementController(ProcurementResultFactory procurementResultFactory,
                                 ProcurementDetailFactory procurementDetailFactory,
                                 ProcurementService procurementService,
                                 OfferController offerController,
                                 CompanyController companyController, AbstractFetcher fetcher){
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
            return true;
        }

        CompletableFuture<Document> detailPageDoc = fetcher.getProcurementDetail(systemNumber);
        CompletableFuture<Document> resultPageDoc = fetcher.getProcurementResult(systemNumber);

        CompletableFuture.allOf(detailPageDoc, resultPageDoc).join();
        ProcurementDetailScrapper procurementDetailScrapper = procurementDetailFactory.create(detailPageDoc.get());
        ProcurementResultScrapper procurementResultScrapper = procurementResultFactory.create(resultPageDoc.get());

        ProcurementInfo procurementInfo = processProcurementData(procurementResultScrapper, procurementDetailScrapper, authority, systemNumber);
        saveProcurementData(authority, systemNumber, procurementInfo);

        return false;
    }

    private ProcurementInfo processProcurementData(ProcurementResultScrapper procurementResultScrapper, ProcurementDetailScrapper procurementDetailScrapper,
                                                   ContractorAuthority contractorAuthority, String systemNumber){
        ProcurementInfo procurementInfo = new ProcurementInfo();
        procurementInfo.procurementName = procurementDetailScrapper.getProcurementName();
        procurementInfo.procurementPlaceOfPerformance = procurementDetailScrapper.getProcurementPlaceOfPerformance();
        procurementInfo.procurementDateOfPublication = procurementDetailScrapper.getProcurementDateOfPublication();

        procurementInfo.participants = procurementResultScrapper.getParticipants();
        procurementInfo.suppliersMap = procurementResultScrapper.getSupplierMap();
        procurementInfo.contractorAuthority = contractorAuthority;
        procurementInfo.systemNumber = systemNumber;

        return procurementInfo;
    }

    private void saveProcurementData(ContractorAuthority authority, String systemNumber, ProcurementInfo procurementInfo){
        procurementInfo.suppliersMap.forEach((supplierName, supplierInfo) -> {
            Company supplier = saveSupplier(supplierInfo);

            Procurement procurement = service.create(new Procurement(
                    procurementInfo.procurementName, supplier, authority,
                    supplierInfo.getContractPrice(), procurementInfo.procurementPlaceOfPerformance,
                    procurementInfo.procurementDateOfPublication, systemNumber));

            saveParticipants(procurement, procurementInfo.participants);
        });
    }

    private Company saveSupplier(ProcurementResultScrapper.CompanyInfo supplierInfo){
        return companyController.saveCompany(supplierInfo.getDetailHref(),
                supplierInfo.getCompanyName());
    }

    private void saveParticipants(Procurement procurement,
                                  ArrayList<ProcurementResultScrapper.CompanyInfo> participants){
        for(ProcurementResultScrapper.CompanyInfo participantInfo : participants){
            Company participant = companyController.saveCompany(participantInfo.getDetailHref(),
                    participantInfo.getCompanyName());
            offerController.saveOffer(participantInfo.getContractPrice(), procurement, participant);
        }
    }
}