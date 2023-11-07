package cz.cvut.fit.bap.parser;

import cz.cvut.fit.bap.parser.controller.ProcurementController;
import cz.cvut.fit.bap.parser.controller.fetcher.FailedFetchException;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
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
    private final ProcurementController procurementController;
    private final Logger logger = LoggerFactory.getLogger(MainScrapper.class);

    @Value("${run.on.startup}") //default value is set to false
    private boolean runOnStartup;

    public MainScrapper(ProcurementController procurementController){
        this.procurementController = procurementController;
    }

    /*
        Run method.
     */
    @Override
    public void run(ApplicationArguments args){
        if(runOnStartup){
            run();
        }
    }

    /**
     * Scheduled start of scrapping.
     */
    @Scheduled(cron = "${scheduling.cron}")
    public void run(){
        int page = 1;
        CompletableFuture<List<String>> systemNumbersFuture = procurementController.getPageSystemNumbers(page);
        while(true){
            List<String> systemNumbers = systemNumbersFuture.join();
            if(systemNumbers.isEmpty()){
                break;
            }
            systemNumbersFuture = procurementController.getPageSystemNumbers(++page);
            scrapeProcurementList(systemNumbers);
        }
    }

    private void scrapeProcurementList(List<String> systemNumbers) {
        for(String procurementSystemNum : systemNumbers){
            try{
                procurementController.save(procurementSystemNum);
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
