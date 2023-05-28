package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.FailedFetchException;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main loop of program
 */
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
    @Scheduled(cron = "${parser.scheduling.cron}")
    public void run(){
        CompletableFuture<List<ContractorAuthorityDto>> authoritiesFuture = contractorAuthorityController.getNextPageAuthorities();
        while(true){
            List<ContractorAuthorityDto> authorities = authoritiesFuture.join();
            if(authorities.isEmpty()){
                break;
            }
            authoritiesFuture = contractorAuthorityController.getNextPageAuthorities();
            scrapeAuthorities(authorities);
        }
    }

    private void scrapeAuthorities(List<ContractorAuthorityDto> authoritiesDto){
        for(ContractorAuthorityDto authorityDto : authoritiesDto){
            ContractorAuthority authority = contractorAuthorityController.saveContractorAuthority(authorityDto);
            scrapeAllProcurements(authority);
        }
    }

    private void scrapeAllProcurements(ContractorAuthority contractorAuthority){
        int page = 1;
        CompletableFuture<List<String>> procurementSystemNumbersFuture = contractorAuthorityController.getProcurementSystemNumbers(contractorAuthority, page++);
        while(true){
            List<String> procurementSystemNumbers = procurementSystemNumbersFuture.join();
            if(procurementSystemNumbers.isEmpty()){
                break;
            }
            procurementSystemNumbersFuture = contractorAuthorityController.getProcurementSystemNumbers(contractorAuthority, page++);
            scrapeProcurementPage(contractorAuthority, procurementSystemNumbers);
        }
    }

    private void scrapeProcurementPage(ContractorAuthority contractorAuthority, List<String> systemNumbers){
        for(String procurementSystemNum : systemNumbers){
            try{
                boolean procurementExists = procurementController.saveProcurement(contractorAuthority, procurementSystemNum);
                if(!procurementExists){
                    break;
                }
            }catch(MissingHtmlElementException | FailedFetchException ignored){
            }
        }
    }
}
