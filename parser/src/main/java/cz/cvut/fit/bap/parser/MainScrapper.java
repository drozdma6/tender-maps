package cz.cvut.fit.bap.parser;

import cz.cvut.fit.bap.parser.controller.ProcurementController;
import cz.cvut.fit.bap.parser.controller.fetcher.FailedFetchException;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Class for starting and scheduling scrapping
 */
@Component
@Profile("!test")
public class MainScrapper {
    private final ProcurementController procurementController;
    private final Logger logger = LoggerFactory.getLogger(MainScrapper.class);

    public MainScrapper(ProcurementController procurementController){
        this.procurementController = procurementController;
    }

    /**
     * Scheduled start of scrapping. Scheduled scrapping begins 30 days after the end of the previous scrapping.
     */
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.DAYS)
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
