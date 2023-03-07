package cz.cvut.fit.bap.parser;

import cz.cvut.fit.bap.parser.scrapper.MainScrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class ParserApplication{
    public static void main(String[] args) throws IOException{
        ApplicationContext ctx = SpringApplication.run(ParserApplication.class, args);
        MainScrapper mainScrapper = ctx.getBean(MainScrapper.class);
        mainScrapper.scrape();
    }
}
