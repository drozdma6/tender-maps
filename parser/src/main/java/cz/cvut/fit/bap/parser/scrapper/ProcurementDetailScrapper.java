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

@Component
public class ProcurementDetailScrapper{
    private final IFetcher fetcher;
    private Document document;
    private final ProcurementService procurementService;


    public ProcurementDetailScrapper(IFetcher fetcher, ProcurementService procurementService){
        this.fetcher = fetcher;
        this.procurementService = procurementService;
    }

    public Procurement scrape(String uri, Company supplier, BigDecimal contractPrice,
                              ContractorAuthority contractorAuthority, String systemNumber)
            throws IOException{
        this.document = fetcher.getProcurementDetail(uri);
        return procurementService.create(
                new Procurement(getProcurementName(), supplier, contractorAuthority, contractPrice,
                                getProcurementPlaceOfPerformance(),
                                getProcurementDateOfPublication(), systemNumber));
    }

    private String getProcurementName(){
        return document.select("h1").text();
    }

    private String getProcurementPlaceOfPerformance(){
        return document.select("[data-title=\"Place of performance\"]").text();
    }

    private LocalDate getProcurementDateOfPublication(){
        String scrappedDate = document.select(
                "[title=\"Date of publication of the tender procedure in the profile\"]").text();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        Pattern pattern = Pattern.compile("[0-9]{2}\\. [0-9]{2}\\. [0-9]{4}");
        Matcher matcher = pattern.matcher(scrappedDate);
        if (!matcher.find()){
            return null;
        }
        String date = matcher.group().replaceAll("\\s", "");
        return LocalDate.parse(date, formatter);
    }
}