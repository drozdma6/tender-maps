package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import kotlin.Pair;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class for handling scrappers
 */
@ConditionalOnProperty(value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true)
@Component
public class MainScrapper{
    private final ContractorAuthorityController contractorAuthorityController;
    private final ProcurementController procurementController;

    public MainScrapper(ContractorAuthorityController contractorAuthorityController, ProcurementController procurementController){
        this.contractorAuthorityController = contractorAuthorityController;
        this.procurementController = procurementController;
    }

    /**
     * Starts scrapping. It is scheduled to run once every 2 weeks
     */
    @Scheduled(fixedRate = 14, timeUnit = TimeUnit.DAYS)
    public void run(){
        List<Pair<String,String>> authorityHrefs = contractorAuthorityController.getContractorAuthorityList();
        for(Pair<String,String> contractorData : authorityHrefs){
            ContractorAuthority authority = contractorAuthorityController.saveContractorAuthority(contractorData.getFirst(), contractorData.getSecond());
            List<String> procurementSystemNums = contractorAuthorityController.getProcurementSystemNumbers(authority);
            for(String procurementSystemNum : procurementSystemNums){
                try{
                    boolean procurementExists = procurementController.saveProcurements(authority, procurementSystemNum);
                    if(procurementExists){
                        break;
                    }
                }catch(MissingHtmlElementException ignored){
                }
            }
        }
    }
}
