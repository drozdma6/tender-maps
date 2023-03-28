package cz.cvut.fit.bap.parser.scrapper.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of IFetcher, used for fetching "<a href="https://nen.nipez.cz">nen.nipez</a>"
 */
@Component
public class NenNipezFetcher implements IFetcher{
    private final String baseUrl = "https://nen.nipez.cz";

    @Value("${procurement.pages.per.fetch}")
    private int procurementPagesPerFetch;  //number of fetched pages per document

    @Value("${procurement.first.date.of.publication}")
    private String firstDateOfPublication; //since when should procurements be scrapped

    /**
     * Fetches contractor detail site.
     *
     * @param profile of contractor
     * @return Document containing contractor detail site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getContractorDetail(String profile) throws IOException{
        final String url = baseUrl + "/en/profily-zadavatelu-platne/detail-profilu/" + profile;
        return Jsoup.connect(url).get();
    }

    /**
     * Fetches document containing list of completed procurements by provided authority profile.
     * Filters only awarded procurements created by given contracting authority and created later
     * than firstDateOfPublication.
     *
     * @param profile                 of contracting authority
     * @param contractorAuthorityName name of contracting authority
     * @param pagingIteration         used to determine which pages are supposed to get fetched
     * @return document containing contractor completed site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getContractorCompleted(String profile, String contractorAuthorityName,
                                           int pagingIteration) throws IOException{
        int rangeEnd = pagingIteration * procurementPagesPerFetch;
        int rangeStart = rangeEnd - (procurementPagesPerFetch - 1);
        String pageRange = rangeStart + "-" + rangeEnd;
        final String url = baseUrl + "/en/profily-zadavatelu-platne/detail-profilu/" + profile +
                           "/uzavrene-zakazky/p:puvz:stavZP=zadana&page=" + pageRange +
                           "&zadavatelNazev=" +
                           UriUtils.encode(contractorAuthorityName, StandardCharsets.UTF_8) +
                           "&datumPrvniUver=" +
                           UriUtils.encode(firstDateOfPublication, StandardCharsets.UTF_8) + ",";

        return Jsoup.connect(url).get();
    }

    /**
     * Fetches procurement result site.
     *
     * @param systemNumber of procurement
     * @return Document containing procurement result site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getProcurementResult(String systemNumber) throws IOException{
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = baseUrl + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen +
                           "/vysledek/p:vys:page=1-100;uca:page=1-100"; //show all participants and suppliers without paging
        return Jsoup.connect(url).get();
    }


    /**
     * Fetches company detail site.
     *
     * @param detailUrl addition to base url for desired company
     * @return Document containing company detail site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getCompanyDetail(String detailUrl) throws IOException{
        return Jsoup.connect(baseUrl + detailUrl).get();
    }


    /**
     * Fetches procurement detail site.
     *
     * @param systemNumber procurement
     * @return Document containing procurement detail site
     * @throws IOException if unable to connect
     */
    @Override
    public Document getProcurementDetail(String systemNumber) throws IOException{
        String systemNumberHyphen = systemNumber.replace('/', '-');
        final String url = baseUrl + "/en/verejne-zakazky/detail-zakazky/" + systemNumberHyphen;
        return Jsoup.connect(url).get();
    }


}
