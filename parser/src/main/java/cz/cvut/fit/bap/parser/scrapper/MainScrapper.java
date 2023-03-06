package cz.cvut.fit.bap.parser.scrapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class MainScrapper{
    private final ContractorCompletedScrapper contractorCompletedScrapper;

    @Value("${authority.profile.file.path}")
    private Resource profilesPath;

    public MainScrapper(ContractorCompletedScrapper contractorCompletedScrapper){
        this.contractorCompletedScrapper = contractorCompletedScrapper;
    }

    /*
          Scrapes all profiles from profiles.txt file -> each profile on separate line
       */
    public void scrape() throws IOException{
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(profilesPath.getInputStream()));

        for (String profile : reader.lines().toList()){
            contractorCompletedScrapper.scrape(profile);
        }
    }
}
