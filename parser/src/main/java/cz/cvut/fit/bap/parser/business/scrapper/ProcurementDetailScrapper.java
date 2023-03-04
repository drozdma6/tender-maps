package cz.cvut.fit.bap.parser.business.scrapper;

import cz.cvut.fit.bap.parser.business.fetcher.IFetcher;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProcurementDetailScrapper{
    private final IFetcher fetcher;

    public ProcurementDetailScrapper(IFetcher fetcher){
        this.fetcher = fetcher;
    }

    public String getProcurementPlaceOfPerformance(String detailUrl) throws IOException{
        Document document = fetcher.getProcurementDetail(detailUrl);
        return document.select("[data-title=\"Place of performance\"]").text();
    }
}