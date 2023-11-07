package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.data.ContractData;
import cz.cvut.fit.bap.parser.controller.data.OfferData;
import cz.cvut.fit.bap.parser.controller.data.ProcurementResultPageData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for scrapping procurement result page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR/uzavrene-zakazky/detail-zakazky/N006-23-V00005185/vysledek">procurement result page</a>
 */
public class ProcurementResultScrapper extends AbstractScrapper<ProcurementResultPageData> {
    public ProcurementResultScrapper(Document document) {
        super(document);
    }

    @Override
    public ProcurementResultPageData getPageData() {
        return new ProcurementResultPageData(getParticipants(), getSuppliers());
    }

    /**
     * Gets information from supplier's table in procurement result page
     *
     * @return list containing data of contracts representing each row in supplier's table
     */
    private List<ContractData> getSuppliers() {
        List<ContractData> suppliers = new ArrayList<>();
        Elements suppliersRows = getSuppliersRows();
        for (Element supplierRow : suppliersRows) {
            suppliers.add(parseContractRow(supplierRow));
        }
        return suppliers;
    }

    /**
     * Gets map containing company detail url as key and additional information about company as value
     *
     * @return data about offers
     */
    private List<OfferData> getParticipants() {
        List<OfferData> participants = new ArrayList<>();
        Elements offerRows = document.select(
                "[title=\"List of participants\"] .gov-table__row");
        for (Element offerRow : offerRows) {
            participants.add(parseOfferRow(offerRow));
        }
        return participants;
    }

    private Elements getSuppliersRows() {
        Elements suppliersRows = document.select(
                "[title=\"Supplier with Whom the Contract Has Been Entered into\"] .gov-table__row");
        if (suppliersRows.isEmpty()) {
            throw new MissingHtmlElementException(document.location() + "is missing supplier row.");
        }
        return suppliersRows;
    }

    private ContractData parseContractRow(Element supplierRow) {
        BigDecimal price = getPrice(supplierRow, "[data-title=\"Contractual price excl. VAT\"]");
        BigDecimal priceVAT = getPrice(supplierRow, "[data-title=\"Contractual price incl. VAT\"]");
        BigDecimal contractPriceWithAmendments = getPrice(supplierRow, "[data-title=\"Aktualized contractual price excl. VAT\"]");
        BigDecimal contractPriceWithAmendmentsVAT = getPrice(supplierRow, "[data-title=\"Aktualized contractual price incl. VAT\"]");
        return new ContractData(price,
                priceVAT,
                contractPriceWithAmendments,
                contractPriceWithAmendmentsVAT,
                getDetailHref(supplierRow),
                getCompanyName(supplierRow),
                getCurrency(supplierRow),
                getContractDate(supplierRow));
    }

    private OfferData parseOfferRow(Element offerRow) {
        BigDecimal price = getPrice(offerRow, "[data-title=\"Bid price excl. VAT\"]");
        BigDecimal priceVAT = getPrice(offerRow, "[data-title=\"Bid price incl. VAT\"]");
        return new OfferData(price, priceVAT, getDetailHref(offerRow), getCompanyName(offerRow), getCurrency(offerRow));

    }

    private String getCompanyName(Element row) {
        Elements nameElem = row.select("[data-title=\"Official name\"]");
        if (nameElem.isEmpty() || !nameElem.hasText()) {
            throw new MissingHtmlElementException(document.location() + "is missing company name.");
        }
        return nameElem.text();
    }

    private BigDecimal getPrice(Element row, String cssQuery) {
        Elements priceElem = row.select(cssQuery);
        if (priceElem.isEmpty() || !priceElem.hasText()) {
            return null;
        }
        return getBigDecimalFromString(priceElem.text());
    }

    private Currency getCurrency(Element row) {
        Elements currencyElem = row.select("[data-title=\"Currency\"]");
        if (currencyElem.isEmpty() || !currencyElem.hasText()) {
            return Currency.CZK;
        }
        return Currency.fromString(currencyElem.text());
    }

    private LocalDate getContractDate(Element row) {
        Elements dateElem = row.select("[data-title=\"Closing date of the contract\"]");
        if (dateElem.isEmpty() || !dateElem.hasText()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        try {
            return LocalDate.parse(dateElem.text(), formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDetailHref(Element companyRow) {
        Elements detailLinkElem = companyRow.select(".gov-link.gov-link--has-arrow");

        if (detailLinkElem.isEmpty() || !detailLinkElem.hasAttr("href")) {
            throw new MissingHtmlElementException(document.location() + "is missing detail link.");
        }
        return removeUrlParameters(detailLinkElem.attr("href"));
    }
}
