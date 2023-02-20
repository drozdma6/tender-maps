package cz.cvut.fit.bap.parser.business.scrapper;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class MainScrapper{
    private final ProfileScrapper profileScrapper;

    public MainScrapper(ProfileScrapper profileScrapper){
        this.profileScrapper = profileScrapper;
    }

    /*
        Scrapes all profiles from profiles.txt file -> each profile on separate line
     */
    @EventListener(ApplicationReadyEvent.class)
    public void scrape() throws IOException{
        String fileName = "src/main/resources/profiles.txt";
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        for (String profile : br.lines().toList()){
            profileScrapper.scrapeProfile(profile);
        }
    }
}
