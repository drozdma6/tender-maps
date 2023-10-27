package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.ProcurementDetailPageData;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Class for scrapping procurement detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206">Procurement detail page</a>
 */
public class ProcurementDetailScrapper extends AbstractScrapper {
    public ProcurementDetailScrapper(Document document) {
        super(document);
    }

    /**
     * Scrapes all data from procurement detail page
     * @return data from procurement detail page
     */
    public ProcurementDetailPageData getPageData() {
        return new ProcurementDetailPageData(
                getProcurementName(),
                getPlaceOfPerformance(),
                getDateOfPublication(),
                getContractingAuthorityData().getFirst(),
                getContractingAuthorityData().getSecond(),
                getType(),
                getTypeOfProcedure(),
                getPublicContractRegime(),
                getBidsSubmissionDeadline(),
                getCodeFromNipezCodeList(),
                getNameFromNipezCodeList());
    }

    /**
     * Gets name of contracting authority and url to contracting authority detail page
     *
     * @return pair of name of contracting authority and url to contracting authority detail page
     */
    private Pair<String, String> getContractingAuthorityData() {
        Elements authorityElem = document.select("[title=\"Contracting authority\"] a");
        if (authorityElem.isEmpty() || !authorityElem.hasText()) {
            throw new MissingHtmlElementException(document.location() + " missing contracting authority info.");
        }
        return Pair.of(authorityElem.text(), authorityElem.attr("href"));
    }

    /**
     * Gets procurement's name from document
     *
     * @return procurement name
     */
    private String getProcurementName() {
        Elements name = document.select("h1");
        if (name.isEmpty() || !name.hasText()) {
            throw new MissingHtmlElementException(document.location() + "is missing procurement name.");
        }
        return name.text();
    }

    /**
     * Gets procurement main place of performance from document
     *
     * @return main place of performance
     */
    private String getPlaceOfPerformance() {
        return getNullIfEmpty(document.select("[title=\"Main place of performance\"] p").text());
    }

    /**
     * Gets procurement date of publication. Function accepts date in format 16. 03. 2023 11:21.
     *
     * @return date or null if date in wrong format
     */
    private LocalDate getDateOfPublication() {
        String scrappedData = document.select(
                "[title=\"Date of publication of the tender procedure in the profile\"] p").text();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy [HH:mm]");
        try {
            return LocalDate.parse(scrappedData, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Gets procurement type.
     *
     * @return type of procurement
     */
    private String getType() {
        return getNullIfEmpty(document.select("[title=\"Type\"] p").text());
    }

    /**
     * Gets procurement type of tender procedure.
     *
     * @return type of tender procedure
     */
    private String getTypeOfProcedure() {
        return getNullIfEmpty(document.select("[title=\"Type of tender procedure\"] p").text());
    }

    /**
     * Gets procurement public contract regime.
     *
     * @return public contract regime
     */
    private String getPublicContractRegime() {
        return getNullIfEmpty(document.select("[title=\"Public contract regime\"] p").text());
    }

    /**
     * Gets procurement bids submission deadline.
     *
     * @return bids submission deadline
     */
    private LocalDate getBidsSubmissionDeadline() {
        String scrappedData = document.select("[title=\"Deadline for the submission of bids\"] p").text();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy [HH:mm]");
        try {
            return LocalDate.parse(scrappedData, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Gets procurement subject code from nipez code list.
     *
     * @return subject code from nipez code list
     */
    private String getCodeFromNipezCodeList() {
        return getNullIfEmpty(document.select("[title=\"Code from the NIPEZ code list\"] p").text());
    }

    /**
     * Gets procurements subject name from nipez code list.
     *
     * @return procurements subject name from nipez code list
     */
    private String getNameFromNipezCodeList() {
        return getNullIfEmpty(document.select("[title=\"Name from the NIPEZ code list\"] p").text());
    }
}