package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.dto.OfferDto;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.*;

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
     * Gets a supplier's map where key is supplier's name and value is additional information scrapped
     * from procurement result page.
     *
     * @return map containing company name as key companyInfo as value
     */
    public Map<String,OfferDto> getSupplierMap(){
        Map<String,OfferDto> suppliersMap = new HashMap<>();
        Elements suppliersRows = getSuppliersRows();

        for(Element supplierRow : suppliersRows){
            String name = getName(supplierRow);
            Optional<BigDecimal> price = getPrice(supplierRow);
            String url = getDetailHref(supplierRow);
            // Check if the company already exists in the map
            suppliersMap.compute(name, (key, existingOffer) -> {
                if(existingOffer != null){
                    // If it exists change value to new OfferDto
                    return existingOffer.addPriceToOffer(price.orElse(null));
                }else{
                    // If it doesn't exist, add the new OfferDto object to the map
                    return new OfferDto(price.orElse(null), url, name);
                }
            });
        }
        return suppliersMap;
    }

    /**
     * Gets map containing company detail url as key and additional information about company as value
     *
     * @return arraylist of CompanyInfo class
     */
    public List<OfferDto> getParticipants(){
        List<OfferDto> participants = new ArrayList<>();
        Elements participantsElems = document.select(
                "[title=\"List of participants\"] .gov-table__row");
        for(Element participantRow : participantsElems){
            String url = getDetailHref(participantRow);
            String strPrice = participantRow.select("[data-title=\"Bid price excl. VAT\"]").text();
            String participantName = participantRow.select("[data-title=\"Official name\"]").text();

            OfferDto offerDto = new OfferDto(getBigDecimalFromString(strPrice),
                    url, participantName);
            participants.add(offerDto);
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
            throw new MissingHtmlElementException();
        }
        return suppliersRows;
    }

    private String getName(Element supplierRow){
        Elements nameElem = supplierRow.select("[data-title=\"Official name\"]");

        if(nameElem.isEmpty() || !nameElem.hasText()){
            throw new MissingHtmlElementException();
        }
        return nameElem.text();
    }

    private Optional<BigDecimal> getPrice(Element supplierRow){
        Elements priceElem = supplierRow.select("[data-title=\"Contractual price excl. VAT\"]");
        if(priceElem.isEmpty() || !priceElem.hasText()){
            return Optional.empty();
        }
        return Optional.of(new BigDecimal(formatPrice(priceElem.text())));
    }

    private String getDetailHref(Element companyRow){
        Elements detailLinkElem = companyRow.select(".gov-link.gov-link--has-arrow");

        if(detailLinkElem.isEmpty() || !detailLinkElem.hasAttr("href")){
            throw new MissingHtmlElementException();
        }
        return removeUrlParameters(detailLinkElem.attr("href"));
    }
}
