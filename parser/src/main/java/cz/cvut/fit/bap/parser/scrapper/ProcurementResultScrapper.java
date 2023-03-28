package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.*;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import kotlin.Pair;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Class for scrapping procurement result page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/MVCR/uzavrene-zakazky/detail-zakazky/N006-23-V00005185/vysledek">procurement result page</a>
 */
@Component
public class ProcurementResultScrapper extends AbstractScrapper{
    private final OfferService offerService;
    private final CompanyDetailScrapper companyDetailScrapper;
    private final ProcurementDetailScrapper procurementDetailScrapper;

    public ProcurementResultScrapper(OfferService offerService, AbstractFetcher fetcher,
                                     CompanyDetailScrapper companyDetailScrapper,
                                     ProcurementDetailScrapper procurementDetailScrapper){
        super(fetcher);
        this.offerService = offerService;
        this.companyDetailScrapper = companyDetailScrapper;
        this.procurementDetailScrapper = procurementDetailScrapper;
    }

    /**
     * Scrapes procurement result page - saves procurement, companies that participated and their offers
     *
     * @param authority    procurement's contractor authority
     * @param systemNumber procurement's system number
     * @throws IOException if wrong url was provided
     */
    public void scrape(ContractorAuthority authority, String systemNumber) throws IOException{
        document = fetcher.getProcurementResult(systemNumber);
        getSupplierMap().forEach((k, v) -> {
            try{
                Company supplier = companyDetailScrapper.scrape(v.getSecond(), k);
                Procurement procurement = procurementDetailScrapper.scrape(supplier, v.getFirst(),
                                                                           authority, systemNumber);
                saveParticipants(procurement);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Puts suppliers in map. Creates a new map element for each new supplier's company name. Values in map
     * are link to company detail and contract price(if there are more rows with similar company makes
     * a sum of their contract price)
     *
     * @return map containing company name as key and contract price and detail link as values
     */
    private HashMap<String, Pair<BigDecimal, String>> getSupplierMap(){
        //storing company name as key, its contract price and detailHref as values
        HashMap<String, Pair<BigDecimal, String>> suppliersMap = new HashMap<>();
        Elements suppliersRows = document.select(
                "[title=\"Supplier with Whom the Contract Has Been Entered into\"] .gov-table__row");
        if (suppliersRows.isEmpty()){
            throw new MissingHtmlElementException();
        }
        for (Element supplierRow : suppliersRows){
            Elements nameElem = supplierRow.select("[data-title=\"Official name\"]");
            Elements priceElem = supplierRow.select("[data-title=\"Contractual price excl. VAT\"]");
            Elements detailLinkElem = supplierRow.select(".gov-link.gov-link--has-arrow");
            //if any information is missing, do not proceed with saving procurement
            if (nameElem.isEmpty() || priceElem.isEmpty() || detailLinkElem.isEmpty()){
                throw new MissingHtmlElementException();
            }

            // Parse the contract price as a BigDecimal and add it to the supplier's existing total,
            // or create a new entry for the supplier, if new supplier was found.
            BigDecimal price = new BigDecimal(formatPrice(priceElem.text()));
            suppliersMap.merge(nameElem.text(), new Pair<>(price, detailLinkElem.attr("href")),
                               (oldValue, newValue) -> new Pair<>(
                                       oldValue.getFirst().add(newValue.getFirst()),
                                       oldValue.getSecond()));
        }
        return suppliersMap;
    }

    /**
     * Saves participant companies as well as their offers to provided procurement
     *
     * @param procurement for which companies make offers
     * @throws IOException if wrong company detail link was found
     */
    private void saveParticipants(Procurement procurement) throws IOException{
        Elements participants = document.select("[title=\"List of participants\"] .gov-table__row");
        for (Element participantRow : participants){
            //only the addition to base url
            String companyDetailHref = participantRow.select("a").attr("href");
            String participantName = participantRow.select("[data-title=\"Official name\"]").text();
            Company participant = companyDetailScrapper.scrape(companyDetailHref, participantName);
            String strPrice = formatPrice(
                    participantRow.select("[data-title=\"Bid price excl. VAT\"]").text());
            saveOffers(strPrice, procurement, participant);
        }
    }

    /**
     * Saves offers.
     *
     * @param strPrice    offer price
     * @param procurement on which offer was created
     * @param company     which created offer
     */
    private void saveOffers(String strPrice, Procurement procurement, Company company){
        OfferId offerId = new OfferId(procurement.getId(), company.getId());
        Offer offer;
        if (!strPrice.isEmpty()){
            offer = new Offer(offerId, new BigDecimal(strPrice), procurement, company);
        } else{
            offer = new Offer(offerId, procurement, company);
        }
        offerService.create(offer);
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
}
