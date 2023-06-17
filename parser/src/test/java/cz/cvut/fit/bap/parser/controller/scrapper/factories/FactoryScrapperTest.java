package cz.cvut.fit.bap.parser.controller.scrapper.factories;

import cz.cvut.fit.bap.parser.controller.scrapper.AbstractScrapper;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/*
    Class for testing all factory scrappers
 */
class FactoryScrapperTest{
    private static Stream<AbstractScrapperFactory<?>> factoryProvider(){
        return Stream.of(
                new CompanyDetailFactory(),
                new ProcurementDetailFactory(),
                new ProcurementResultFactory(),
                new ContractorDetailFactory(),
                new ProcurementListFactory());
        // Add other factory instances here
    }

    @ParameterizedTest
    @MethodSource("factoryProvider")
    void testFactoryCreation(AbstractScrapperFactory<?> factory){
        Document document = new Document("testDocument");

        Object scrapper = factory.create(document);

        Assertions.assertNotNull(scrapper);
        Assertions.assertTrue(scrapper instanceof AbstractScrapper);
    }
}

