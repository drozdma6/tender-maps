package cz.cvut.fit.bap.parser.scrapper.fetcher;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


@SpringBootTest
@TestPropertySource(
        properties = {"procurement.first.date.of.publication=2022-01-01", "procurement.pages.per.fetch=5"})
class NenNipezFetcherTest{

    @MockBean
    private Connection connection;

    @Autowired
    private NenNipezFetcher nenNipezFetcher;

    @Test
    void getContractorDetail() throws IOException{
        String profile = "testProfile";
        String expectedUrl =
                "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/" + profile;
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try (MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc, nenNipezFetcher.getContractorDetail(profile));
        }
    }

    @Test
    void getContractorCompleted() throws IOException{
        String expectedUrl = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/testProfile/uzavrene-zakazky/p:puvz:stavZP=zadana&page=21-25&zadavatelNazev=test%20Name&datumPrvniUver=2022-01-01,";
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try (MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc,
                                    nenNipezFetcher.getContractorCompleted("testProfile",
                                                                           "test Name", 5));
        }
    }

    @Test
    void getProcurementResult() throws IOException{
        String testSystemNumber = "testSystemNumber";
        String expectedUrl = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/testSystemNumber/vysledek/p:vys:page=1-10;uca:page=1-10";
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try (MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc,
                                    nenNipezFetcher.getProcurementResult(testSystemNumber));
        }
    }

    @Test
    void getCompanyDetail() throws IOException{
        String testUrl = "/testUrl";
        String expectedUrl = "https://nen.nipez.cz/testUrl";
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try (MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc, nenNipezFetcher.getCompanyDetail(testUrl));
        }
    }

    @Test
    void getProcurementDetail() throws IOException{
        String testSystemNumber = "test/System/-Number";
        String expectedUrl = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/test-System--Number";
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try (MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc,
                                    nenNipezFetcher.getProcurementDetail(testSystemNumber));
        }
    }
}