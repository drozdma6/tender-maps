package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.domain.Procurement;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Class for scrapping procurement detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206">Procurement detail page</a>
 */
@Component
public class ProcurementDetailScrapper extends AbstractScrapper{
    private final ProcurementService procurementService;


    public ProcurementDetailScrapper(AbstractFetcher fetcher,
                                     ProcurementService procurementService){
        super(fetcher);
        this.procurementService = procurementService;
    }

    /**
     * Scrapes procurement detail page
     *
     * @param supplier            procurement's supplier
     * @param contractPrice       procurement's price
     * @param contractorAuthority procurement's authority
     * @param systemNumber        procurement's system number
     * @return saved procurement
     * @throws IOException if wrong url was provided
     */
    public Procurement scrape(Company supplier, BigDecimal contractPrice,
                              ContractorAuthority contractorAuthority, String systemNumber)
            throws IOException{
        document = fetcher.getProcurementDetail(systemNumber);
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
        return getNullIfEmpty(document.select("[data-title=\"Place of performance\"]").text());
    }

    /**
     * Gets procurement date of publication. Function accepts date in format 16. 03. 2023 11:21).
     *
     * @return date or null
     */
    private LocalDate getProcurementDateOfPublication(){
        String scrappedData = document.select(
                "[title=\"Date of publication of the tender procedure in the profile\"] p").text();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy [HH:mm]");
        try{
            return LocalDate.parse(scrappedData, formatter);
        } catch (DateTimeParseException e){
            return null;
        }
    }
}