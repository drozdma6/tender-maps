package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Class for handling scrappers
 */
@Component
public class MainScrapper{
    private final ContractorCompletedScrapper contractorCompletedScrapper;
    private final ContractorDetailScrapper contractorDetailScrapper;

    @Value("${authority.profile.file.path}")
    private Resource profilesPath;

    public MainScrapper(ContractorCompletedScrapper contractorCompletedScrapper,
                        ContractorDetailScrapper contractorDetailScrapper){
        this.contractorCompletedScrapper = contractorCompletedScrapper;
        this.contractorDetailScrapper = contractorDetailScrapper;
    }

    /**
     * Scrapes all profiles from profilesPath
     * This method is scheduled to run every 2 weeks
     *
     * @throws IOException if the content stream could not be opened
     */
    @Scheduled(fixedRate = 14, timeUnit = TimeUnit.DAYS)
    public void scrape() throws IOException{
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(profilesPath.getInputStream()));

        for (String profile : reader.lines().toList()){
            ContractorAuthority authority = contractorDetailScrapper.scrape(profile);
            contractorCompletedScrapper.scrape(authority);
        }
    }
}
