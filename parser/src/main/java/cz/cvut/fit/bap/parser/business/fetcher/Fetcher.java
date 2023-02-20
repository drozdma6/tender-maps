package cz.cvut.fit.bap.parser.business.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Fetcher implements IFetcher{
    private final String baseUrl = "https://nen.nipez.cz";

    @Override
    public Document getProfile(String profile) throws IOException{
        final String mfcrCompletedUrl =
                "/en/profily-zadavatelu-platne/detail-profilu/" + profile + "/uzavrene-zakazky";
        return Jsoup.connect(baseUrl + mfcrCompletedUrl).get();
    }

    @Override
    public Document getDetail(String detailUrl) throws IOException{
        return Jsoup.connect(baseUrl + detailUrl + "/vysledek").get();
    }
}
