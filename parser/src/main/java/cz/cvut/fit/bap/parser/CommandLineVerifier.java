package cz.cvut.fit.bap.parser;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@Component
class CommandLineVerifier implements CommandLineRunner{
    @Override
    public void run(String... args){
        // Check and process each command line argument
        for (String arg : args){
            if (arg.startsWith("--procurement.pages.per.fetch=")){
                processPagesPerFetchArgument(arg);
            } else if (arg.startsWith("--procurement.scrape.all=")){
                processScrapeAllArgument(arg);
            } else if (arg.startsWith("--procurement.first.date.of.publication=")){
                processFirstDateOfPublicationArgument(arg);
            }
        }
    }

    private void processPagesPerFetchArgument(String arg){
        String value = arg.substring("--procurement.pages.per.fetch=".length());
        try{
            if (Integer.parseInt(value) <= 0){
                throw new IllegalArgumentException(
                        "The procurement.pages.per.fetch value must be a positive integer.");
            }

        } catch (NumberFormatException e){
            throw new IllegalArgumentException(
                    "The procurement.pages.per.fetch value is not a valid integer: " + value);
        }
    }

    private void processScrapeAllArgument(String arg){
        String value = arg.substring("--procurement.scrape.all=".length());
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
            throw new IllegalArgumentException(
                    "The procurement.scrape.all value is not a valid boolean: " + value);
        }
    }

    private void processFirstDateOfPublicationArgument(String arg){
        String value = arg.substring("--procurement.first.date.of.publication=".length());
        try{
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e){
            throw new IllegalArgumentException(
                    "The procurement.first.date.of.publication value is not a valid date in format YYYY-MM-DD: " +
                    value);
        }
    }
}