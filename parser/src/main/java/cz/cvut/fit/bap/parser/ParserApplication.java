package cz.cvut.fit.bap.parser;

import cz.cvut.fit.bap.parser.scrapper.MainScrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class ParserApplication{
    public static void main(String[] args) throws IOException{
        ApplicationContext ctx = SpringApplication.run(ParserApplication.class, args);
        MainScrapper mainScrapper = ctx.getBean(MainScrapper.class);
        mainScrapper.scrape();
    }
}
