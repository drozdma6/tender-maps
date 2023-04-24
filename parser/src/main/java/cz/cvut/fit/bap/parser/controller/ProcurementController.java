package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Procurement;
import cz.cvut.fit.bap.parser.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.scrapper.factories.ProcurementDetailFactory;
import cz.cvut.fit.bap.parser.scrapper.factories.ProcurementResultFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/*
    Controller handling communication with procurement service and required scrappers to get procurement
 */
@Component
public class ProcurementController extends AbstractController<ProcurementService>{
    private final ProcurementResultFactory procurementResultFactory;
    private final ProcurementDetailFactory procurementDetailFactory;
    private final OfferController offerController;
    private final CompanyController companyController;


    public ProcurementController(ProcurementResultFactory procurementResultFactory,
                                 ProcurementDetailFactory procurementDetailFactory,
                                 ProcurementService procurementService,
                                 OfferController offerController,
                                 CompanyController companyController){
        super(procurementService);
        this.procurementResultFactory = procurementResultFactory;
        this.procurementDetailFactory = procurementDetailFactory;
        this.offerController = offerController;
        this.companyController = companyController;
    }

    /**
     * Saves procurements. If there are multiple suppliers for single procurement creates and saves
     * new Procurement for each supplier, so that one procurement always has single supplier.
     *
     * @param authority    contracting authority of procurement
     * @param systemNumber procurement system number
     * @return true if procurement is already in database, false otherwise
     */
    public boolean saveProcurements(ContractorAuthority authority, String systemNumber){
        if (service.existsBySystemNumber(systemNumber)){
            return true;
        }
        ProcurementDetailScrapper procurementDetailScrapper = procurementDetailFactory.create(
                systemNumber);
        String procurementName = procurementDetailScrapper.getProcurementName();
        String procurementPlaceOfPerformance = procurementDetailScrapper.getProcurementPlaceOfPerformance();
        LocalDate procurementDateOfPublication = procurementDetailScrapper.getProcurementDateOfPublication();

        ProcurementResultScrapper procurementResultScrapper = procurementResultFactory.create(
                systemNumber);
        ArrayList<ProcurementResultScrapper.CompanyInfo> participants = procurementResultScrapper.getParticipants();
        HashMap<String, ProcurementResultScrapper.CompanyInfo> suppliersMap = procurementResultScrapper.getSupplierMap();

        suppliersMap.forEach((supplierName, supplierInfo) -> {
            Company supplier = saveSupplier(supplierInfo);
            Procurement procurement = saveProcurement(authority, systemNumber, procurementName,
                                                      procurementPlaceOfPerformance,
                                                      procurementDateOfPublication, supplier,
                                                      supplierInfo.getContractPrice());
            saveParticipants(procurement, participants);
        });
        return false;
    }

    private Company saveSupplier(ProcurementResultScrapper.CompanyInfo supplierInfo){
        return companyController.saveCompany(supplierInfo.getDetailHref(),
                                             supplierInfo.getCompanyName());
    }

    private Procurement saveProcurement(ContractorAuthority authority, String systemNumber,
                                        String procurementName,
                                        String procurementPlaceOfPerformance,
                                        LocalDate procurementDateOfPublication, Company supplier,
                                        BigDecimal contractPrice){
        return service.create(new Procurement(procurementName, supplier, authority, contractPrice,
                                              procurementPlaceOfPerformance,
                                              procurementDateOfPublication, systemNumber));
    }

    private void saveParticipants(Procurement procurement,
                                  ArrayList<ProcurementResultScrapper.CompanyInfo> participants){
        for (ProcurementResultScrapper.CompanyInfo participantInfo : participants){
            Company participant = companyController.saveCompany(participantInfo.getDetailHref(),
                                                                participantInfo.getCompanyName());
            offerController.saveOffer(participantInfo.getContractPrice(), procurement, participant);
        }
    }
}