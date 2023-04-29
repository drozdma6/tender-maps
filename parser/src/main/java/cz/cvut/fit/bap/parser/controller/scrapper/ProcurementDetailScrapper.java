package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Class for scrapping procurement detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206">Procurement detail page</a>
 */
public class ProcurementDetailScrapper extends AbstractScrapper{
    public ProcurementDetailScrapper(Document document){
        super(document);
    }

    /**
     * Gets procurement's name from document
     *
     * @return procurement name
     */
    public String getProcurementName(){
        return document.select("h1").text();
    }

    /**
     * Gets procurement place of performance from document
     *
     * @return place of performance
     */
    public String getProcurementPlaceOfPerformance(){
        return getNullIfEmpty(document.select("[data-title=\"Place of performance\"]").text());
    }

    /**
     * Gets procurement date of publication. Function accepts date in format 16. 03. 2023 11:21.
     *
     * @return date or null if date in wrong format
     */
    public LocalDate getProcurementDateOfPublication(){
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