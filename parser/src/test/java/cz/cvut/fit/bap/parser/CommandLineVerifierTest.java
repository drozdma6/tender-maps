package cz.cvut.fit.bap.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class CommandLineVerifierTest{
    private CommandLineVerifier commandLineVerifier;

    @BeforeEach
    public void setUp(){
        commandLineVerifier = new CommandLineVerifier();
    }

    @Test
    public void pagesPerFetch(){
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> commandLineVerifier.run("--procurement.scrape.all=true",
                                                              "--procurement.pages.per.fetch=-5"));

        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> commandLineVerifier.run("--procurement.pages.per.fetch=0"));

        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> commandLineVerifier.run("--procurement.pages.per.fetch=abc"));
    }

    @Test
    public void scrapeAll(){
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> commandLineVerifier.run("--procurement.scrape.all=fales"));

        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> commandLineVerifier.run("--procurement.scrape.all=abc"));
    }

    @Test
    public void firstDateOfPublication(){
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> commandLineVerifier.run("--procurement.scrape.all=false",
                                                              "--procurement.first.date.of.publication=abc"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> commandLineVerifier.run(
                "--procurement.first.date.of.publication=2022/02/01"));


        Assertions.assertThrows(IllegalArgumentException.class, () -> commandLineVerifier.run(
                "--procurement.first.date.of.publication=2022-2-01"));


        Assertions.assertThrows(IllegalArgumentException.class, () -> commandLineVerifier.run(
                "--procurement.first.date.of.publication=1.1.2022"));
    }
}