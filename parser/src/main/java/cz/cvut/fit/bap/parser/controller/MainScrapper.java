package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.FailedFetchException;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main loop of program
 */
@Component
public class MainScrapper implements ApplicationRunner{
    private final ContractorAuthorityController contractorAuthorityController;
    private final ProcurementController procurementController;
    private final Logger logger = LoggerFactory.getLogger(MainScrapper.class);

    @Value("${RUN_ON_STARTUP:false}") //default value is set to false
    private boolean runOnStartup;

    public MainScrapper(ContractorAuthorityController contractorAuthorityController, ProcurementController procurementController){
        this.contractorAuthorityController = contractorAuthorityController;
        this.procurementController = procurementController;
    }

    @Override
    public void run(ApplicationArguments args){
        if(runOnStartup){
            run();
        }
    }

    /**
     * Scheduled start of scrapping.
     */
    @Scheduled(cron = "${SCHEDULING_CRON}")
    public void run(){
        int page = 1;
        CompletableFuture<List<ContractorAuthorityDto>> authoritiesFuture = contractorAuthorityController.getAuthoritiesPage(page);
        while(true){
            List<ContractorAuthorityDto> authorities = authoritiesFuture.join();
            if(authorities.isEmpty()){
                break;
            }
            authoritiesFuture = contractorAuthorityController.getAuthoritiesPage(++page);
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
            }catch(MissingHtmlElementException e){
                Metrics.counter("scrapper.skipped.procurements").increment();
                logger.debug(e.getMessage());
            }catch(FailedFetchException e){
                Metrics.counter("scrapper.failed.fetch.").increment();
                logger.debug(e.getMessage());
            }
        }
    }
}
