package cz.cvut.fit.bap.parser.business.scrapper;

import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class MainScrapper{
    private final ContractorCompletedScrapper contractorCompletedScrapper;

    public MainScrapper(ContractorCompletedScrapper contractorCompletedScrapper){
        this.contractorCompletedScrapper = contractorCompletedScrapper;
    }

    /*
        Scrapes all profiles from profiles.txt file -> each profile on separate line
     */
    public void scrape() throws IOException{
        String fileName = "src/main/resources/profiles.txt";
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        for (String profile : br.lines().toList()){
            contractorCompletedScrapper.scrapeContractorCompleted(profile);
        }
    }
}
