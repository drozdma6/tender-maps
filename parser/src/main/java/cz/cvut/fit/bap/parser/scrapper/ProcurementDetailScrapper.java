package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Procurement;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for scrapping procurement detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206">Procurement detail page</a>
 */
@Component
public class ProcurementDetailScrapper{
    private final IFetcher fetcher;
    private Document document;
    private final ProcurementService procurementService;


    public ProcurementDetailScrapper(IFetcher fetcher, ProcurementService procurementService){
        this.fetcher = fetcher;
        this.procurementService = procurementService;
    }

    /**
     * Scrapes procurement detail page
     *
     * @param url                 of procurement detail page
     * @param supplier            procurement's supplier
     * @param contractPrice       procurement's price
     * @param contractorAuthority procurement's authority
     * @param systemNumber        procurement's system number
     * @return saved procurement
     * @throws IOException if wrong url was provided
     */
    public Procurement scrape(String url, Company supplier, BigDecimal contractPrice,
                              ContractorAuthority contractorAuthority, String systemNumber)
            throws IOException{
        this.document = fetcher.getProcurementDetail(url);
        return procurementService.create(
                new Procurement(getProcurementName(), supplier, contractorAuthority, contractPrice,
                                getProcurementPlaceOfPerformance(),
                                getProcurementDateOfPublication(), systemNumber));
    }

    /**
     * Gets procurement's name from document
     *
     * @return procurement name
     */
    private String getProcurementName(){
        return document.select("h1").text();
    }

    /**
     * Gets procurement place of performance from document
     *
     * @return place of performance
     */
    private String getProcurementPlaceOfPerformance(){
        return document.select("[data-title=\"Place of performance\"]").text();
    }

    /**
     * Gets procurement date of publication
     *
     * @return date in format dd.mm.yyy or null
     */
    private LocalDate getProcurementDateOfPublication(){
        String scrappedDate = document.select(
                "[title=\"Date of publication of the tender procedure in the profile\"]").text();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        Pattern pattern = Pattern.compile("\\d{2}\\. \\d{2}\\. \\d{4}");
        Matcher matcher = pattern.matcher(scrappedDate);
        if (!matcher.find()){
            return null;
        }
        String date = matcher.group().replaceAll("\\s", "");
        return LocalDate.parse(date, formatter);
    }
}