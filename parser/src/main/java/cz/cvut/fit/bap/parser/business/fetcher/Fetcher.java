package cz.cvut.fit.bap.parser.business.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Fetcher implements IFetcher{
    private final String baseUrl = "https://nen.nipez.cz";

    @Override
    public Document getContractorDetail(String profile) throws IOException{
        final String mfcrCompletedUrl = "/en/profily-zadavatelu-platne/detail-profilu/" + profile;
        return Jsoup.connect(baseUrl + mfcrCompletedUrl).get();
    }

    @Override
    public Document getContractorCompleted(String profile) throws IOException{
        final String mfcrCompletedUrl =
                "/en/profily-zadavatelu-platne/detail-profilu/" + profile + "/uzavrene-zakazky";
        return Jsoup.connect(baseUrl + mfcrCompletedUrl).get();
    }

    @Override
    public Document getProcurementResult(String detailUrl) throws IOException{
        return Jsoup.connect(baseUrl + detailUrl + "/vysledek").get();
    }

    @Override
    public Document getCompanyDetail(String detailUrl) throws IOException{
        return Jsoup.connect(baseUrl + detailUrl).get();
    }
}
