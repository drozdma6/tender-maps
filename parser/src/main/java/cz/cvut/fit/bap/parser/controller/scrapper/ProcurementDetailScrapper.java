package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.dto.ContractingAuthorityDto;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
     * Gets information about contracting authority who created this procurement
     *
     * @return contractingAuthorityDto of authority
     */
    public ContractingAuthorityDto getContractingAuthorityDto() {
        Elements authorityElem = document.select("[title=\"Contracting authority\"] a");
        if(authorityElem.isEmpty() || !authorityElem.hasText()){
            throw new MissingHtmlElementException(document.location() + " missing contracting authority info.");
        }
        String url = authorityElem.attr("href");
        String name = authorityElem.text();
        return new ContractingAuthorityDto(url, name);
    }

    /**
     * Gets procurement's name from document
     *
     * @return procurement name
     */
    public String getProcurementName(){
        Elements name = document.select("h1");
        if(name.isEmpty() || !name.hasText()){
            throw new MissingHtmlElementException(document.location() + "is missing procurement name.");
        }
        return name.text();
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
        }catch(DateTimeParseException e){
            return null;
        }
    }
}