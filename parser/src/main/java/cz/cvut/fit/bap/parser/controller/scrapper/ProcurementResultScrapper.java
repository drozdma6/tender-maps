package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for scrapping procurement result page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR/uzavrene-zakazky/detail-zakazky/N006-23-V00005185/vysledek">procurement result page</a>
 */
public class ProcurementResultScrapper extends AbstractScrapper{
    public ProcurementResultScrapper(Document document){
        super(document);
    }

    /*
        Class represents Company information scrapped from ProcurementResult page
     */
    public static class CompanyInfo{
        private BigDecimal contractPrice;
        private final String detailHref;
        private final String companyName;

        public CompanyInfo(BigDecimal contractPrice, String detailHref, String companyName){
            this.contractPrice = contractPrice;
            this.detailHref = detailHref;
            this.companyName = companyName;
        }

        public String getCompanyName(){
            return companyName;
        }

        public BigDecimal getContractPrice(){
            return contractPrice;
        }

        public String getDetailHref(){
            return detailHref;
        }

        public void addContractPrice(BigDecimal price){
            this.contractPrice = this.contractPrice.add(price);
        }
    }

    /**
     * Gets a supplier's map where key is supplier's name and value is additional information scrapped
     * from procurement result page.
     *
     * @return map containing company name as key companyInfo as value
     */
    public HashMap<String, CompanyInfo> getSupplierMap(){
        HashMap<String, CompanyInfo> suppliersMap = new HashMap<>();
        Elements suppliersRows = getSuppliersRows();

        for (Element supplierRow : suppliersRows){
            String name = getName(supplierRow);
            BigDecimal price = getPrice(supplierRow);
            String detailHref = getDetailHref(supplierRow);

            // Check if the company already exists in the map
            CompanyInfo existingCompanyInfo = suppliersMap.get(name);
            if (existingCompanyInfo != null){
                // If it exists, update the existing contract price
                existingCompanyInfo.addContractPrice(price);
            } else{
                // If it doesn't exist, add the new CompanyInfo object to the map
                suppliersMap.put(name, new CompanyInfo(price, detailHref, name));
            }
        }
        return suppliersMap;
    }

    /**
     * Gets arraylist containing information about participants scrapped from procurement result page
     *
     * @return arraylist of CompanyInfo class
     */
    public ArrayList<CompanyInfo> getParticipants(){
        ArrayList<CompanyInfo> participants = new ArrayList<>();
        Elements participantsElems = document.select(
                "[title=\"List of participants\"] .gov-table__row");
        for (Element participantRow : participantsElems){

            String companyDetailHref = participantRow.select("a").attr("href");
            String strPrice = formatPrice(
                    participantRow.select("[data-title=\"Bid price excl. VAT\"]").text());
            String participantName = participantRow.select("[data-title=\"Official name\"]").text();

            CompanyInfo companyInfo = new CompanyInfo(getBigDecimalFromString(strPrice),
                                                      companyDetailHref, participantName);
            participants.add(companyInfo);
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
            return new BigDecimal(price);
        } catch (NumberFormatException e){
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

        if (suppliersRows.isEmpty()){
            throw new MissingHtmlElementException();
        }
        return suppliersRows;
    }

    private String getName(Element supplierRow){
        Elements nameElem = supplierRow.select("[data-title=\"Official name\"]");

        if (nameElem.isEmpty()){
            throw new MissingHtmlElementException();
        }
        return nameElem.text();
    }

    private BigDecimal getPrice(Element supplierRow){
        Elements priceElem = supplierRow.select("[data-title=\"Contractual price excl. VAT\"]");

        if (priceElem.isEmpty()){
            throw new MissingHtmlElementException();
        }
        return new BigDecimal(formatPrice(priceElem.text()));
    }

    private String getDetailHref(Element supplierRow){
        Elements detailLinkElem = supplierRow.select(".gov-link.gov-link--has-arrow");

        if (detailLinkElem.isEmpty()){
            throw new MissingHtmlElementException();
        }
        return detailLinkElem.attr("href");
    }
}
