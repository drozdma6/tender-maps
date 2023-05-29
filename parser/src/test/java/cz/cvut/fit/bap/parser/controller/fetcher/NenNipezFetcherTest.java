package cz.cvut.fit.bap.parser.controller.fetcher;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NenNipezFetcherTest{
    @Mock
    private Connection connection;

    @InjectMocks
    private NenNipezFetcher nenNipezFetcher;

    @Test
    void getContractorDetail() throws IOException{
        String testUrl = "/testUrl";
        String expectedUrl =
                "https://nen.nipez.cz/en" + testUrl;

        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try(MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc, nenNipezFetcher.getContractorDetail(testUrl));
        }
    }

    @Test
    void getContractorCompleted() throws IOException{
        String testUrl = "/testUrl";
        int page = 1;
        String expectedUrl = "https://nen.nipez.cz/en" + testUrl + "/uzavrene-zakazky/p:puvz:stavZP=zadana&page=" + page;
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try(MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc,
                    nenNipezFetcher.getContractorCompleted(testUrl, page));
        }
    }

    @Test
    void getProcurementResult() throws IOException{
        String testSystemNumber = "testSystemNumber";
        String expectedUrl = "https://nen.nipez.cz/en/verejne-zakazky/detail-zakazky/testSystemNumber/vysledek/p:vys:page=1-10;uca:page=1-10";
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try(MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
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
        try(MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
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
        try(MockedStatic<Jsoup> jsoup = Mockito.mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Document actualDocument = nenNipezFetcher.getProcurementDetail(testSystemNumber).join();

            Assertions.assertEquals(expectedDoc, actualDocument);
            verify(connection, times(1)).get();
        }
    }

    @Test
    void getContractorAuthorityList() throws IOException{
        int page = 1;
        String expectedUrl = "https://nen.nipez.cz/profily-zadavatelu-platne/p:pzp:page=" + page;
        Document expectedDoc = new Document(expectedUrl);

        when(connection.get()).thenReturn(expectedDoc);
        try(MockedStatic<Jsoup> jsoup = mockStatic(Jsoup.class)){
            jsoup.when(() -> Jsoup.connect(expectedUrl)).thenReturn(connection);
            Assertions.assertEquals(expectedDoc, nenNipezFetcher.getContractorAuthorityList(page));
        }
    }
}
