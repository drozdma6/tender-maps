package cz.cvut.fit.bap.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParserApplication{
    public static void main(String[] args){
        SpringApplication.run(ParserApplication.class, args);
    }
}
