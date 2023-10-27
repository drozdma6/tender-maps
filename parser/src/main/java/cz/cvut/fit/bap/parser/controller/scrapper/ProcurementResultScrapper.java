package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.data.ContractData;
import cz.cvut.fit.bap.parser.controller.data.OfferData;
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
public class ProcurementResultScrapper extends AbstractScrapper{
    public ProcurementResultScrapper(Document document){
        super(document);
    }

    /**
     * Gets information from supplier's table in procurement result page
     *
     * @return list containing data of contracts representing each row in supplier's table
     */
    public List<ContractData> getSuppliers() {
        List<ContractData> suppliers = new ArrayList<>();
        Elements suppliersRows = getSuppliersRows();
        for(Element supplierRow : suppliersRows){
            suppliers.add(new ContractData(
                    getPrice(supplierRow),
                    getDetailHref(supplierRow),
                    getSupplierName(supplierRow),
                    getCurrency(supplierRow),
                    getContractDate(supplierRow)
            ));
        }
        return suppliers;
    }

    /**
     * Gets map containing company detail url as key and additional information about company as value
     *
     * @return data about offers
     */
    public List<OfferData> getParticipants(){
        List<OfferData> participants = new ArrayList<>();
        Elements participantsElems = document.select(
                "[title=\"List of participants\"] .gov-table__row");
        for(Element participantRow : participantsElems){
            String url = getDetailHref(participantRow);
            String strPrice = participantRow.select("[data-title=\"Bid price excl. VAT\"]").text();
            String participantName = participantRow.select("[data-title=\"Official name\"]").text();
            Currency currency = getCurrency(participantRow);
            OfferData offerData = new OfferData(getBigDecimalFromString(strPrice),
                    url, participantName, currency);
            participants.add(offerData);
        }
        return participants;
    }

    /**
     * Gets BigDecimal from string.
     *
     * @param price which is supposed to be converted
     * @return BigDecimal or null if string can not be converted
     */
    private BigDecimal getBigDecimalFromString(String price){
        try{
            return new BigDecimal(formatPrice(price));
        }catch(NumberFormatException e){
            return null;
        }
    }

    /**
     * Format string price into format accepted by BigDecimal
     *
     * @param strPrice price which is supposed to by formatted
     * @return formatted price
     */
    private String formatPrice(String strPrice){
        return strPrice.replaceAll("\\s", "").replace(',', '.');
    }

    private Elements getSuppliersRows(){
        Elements suppliersRows = document.select(
                "[title=\"Supplier with Whom the Contract Has Been Entered into\"] .gov-table__row");
        if(suppliersRows.isEmpty()){
            throw new MissingHtmlElementException(document.location() + "is missing supplier row.");
        }
        return suppliersRows;
    }

    private String getSupplierName(Element supplierRow){
        Elements nameElem = supplierRow.select("[data-title=\"Official name\"]");
        if(nameElem.isEmpty() || !nameElem.hasText()){
            throw new MissingHtmlElementException(document.location() + "is missing supplier name.");
        }
        return nameElem.text();
    }

    private BigDecimal getPrice(Element supplierRow) {
        Elements priceElem = supplierRow.select("[data-title=\"Contractual price excl. VAT\"]");
        if(priceElem.isEmpty() || !priceElem.hasText()){
            return null;
        }
        return new BigDecimal(formatPrice(priceElem.text()));
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

    private String getDetailHref(Element companyRow){
        Elements detailLinkElem = companyRow.select(".gov-link.gov-link--has-arrow");

        if(detailLinkElem.isEmpty() || !detailLinkElem.hasAttr("href")){
            throw new MissingHtmlElementException(document.location() + "is missing detail link.");
        }
        return removeUrlParameters(detailLinkElem.attr("href"));
    }
}
