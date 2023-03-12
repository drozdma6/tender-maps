package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.*;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

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

    public ProcurementResultScrapper(OfferService offerService, IFetcher fetcher,
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
     * @param url          to procurement result
     * @param authority    procurement's contractor authority
     * @param systemNumber procurement's system number
     * @throws IOException if wrong url was provided
     */
    public void scrape(String url, ContractorAuthority authority, String systemNumber)
            throws IOException{
        document = fetcher.getProcurementResult(url);
        Procurement procurement = procurementDetailScrapper.scrape(url, saveSupplier(),
                                                                   getContractPrice(), authority,
                                                                   systemNumber);
        if (!hasSingleSupplier()){
            throw new RuntimeException("Found procurement which has several suppliers: " +
                                       procurement.getSystemNumber());
        }
        saveParticipants(procurement);
    }

    /**
     * Checks if procurement has a single company as supplier
     *
     * @return true if procurement has single company as supplier, false otherwise
     */
    private boolean hasSingleSupplier(){
        Elements supplier = document.select(
                "[title=\"Supplier with Whom the Contract Has Been Entered into\"] td");
        Elements supplierCompanyNames = supplier.select("[data-title=\"Official name\"]");
        if (supplierCompanyNames.isEmpty()){
            throw new MissingHtmlElementException();
        }
        for (Element supplierCompanyName : supplierCompanyNames){
            if (!supplierCompanyName.text().equals(supplierCompanyNames.first().text())){
                return false;
            }
        }
        return true;
    }

    /**
     * Parse supplier's contract price, if more contracts were made with supplier makes a sum
     *
     * @return sum of all contract prices with supplier
     */
    private BigDecimal getContractPrice(){
        Elements priceElements = document.select("[data-title=\"Contractual price excl. VAT\"]");
        BigDecimal contractPriceSum = new BigDecimal(0);
        if (priceElements.isEmpty()){
            throw new MissingHtmlElementException();
        }
        for (Element elemPrice : priceElements){
            String strPrice = formatPrice(elemPrice.text());
            contractPriceSum = new BigDecimal(strPrice).add(contractPriceSum);
        }
        return contractPriceSum;
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
            Company participant = companyDetailScrapper.scrape(companyDetailHref);
            String strPrice = formatPrice(
                    participantRow.select("[data-title=\"Bid price excl. VAT\"]").text());
            saveOffers(strPrice, procurement, participant);
        }
    }

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
     * Saves supplier.
     *
     * @return supplier company
     * @throws IOException if wrong company detail link was found
     */
    private Company saveSupplier() throws IOException{
        Elements supplier = document.select(
                "[title=\"Supplier with Whom the Contract Has Been Entered into\"] td");
        //only the addition to base url
        String companyDetailHref = supplier.select("td.gov-table__cell:nth-of-type(1) a")
                .attr("href");
        if (companyDetailHref.isEmpty()){
            throw new MissingHtmlElementException();
        }
        return companyDetailScrapper.scrape(companyDetailHref);
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
