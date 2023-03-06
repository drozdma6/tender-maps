package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.*;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class ProcurementResultScrapper{
    private final OfferService offerService;
    private final IFetcher fetcher;
    private final CompanyDetailScrapper companyDetailScrapper;
    private final ProcurementDetailScrapper procurementDetailScrapper;
    private Document document;

    public ProcurementResultScrapper(OfferService offerService, IFetcher fetcher,
                                     CompanyDetailScrapper companyDetailScrapper,
                                     ProcurementDetailScrapper procurementDetailScrapper){
        this.offerService = offerService;
        this.fetcher = fetcher;
        this.companyDetailScrapper = companyDetailScrapper;
        this.procurementDetailScrapper = procurementDetailScrapper;
    }

    public void scrape(String url, ContractorAuthority authority, String systemNumber)
            throws IOException{
        document = fetcher.getProcurementResult(url);
        Procurement procurement = procurementDetailScrapper.scrape(url, saveSupplier(),
                                                                   getContractPrice(), authority,
                                                                   systemNumber);
        saveParticipants(procurement);
    }

    private BigDecimal getContractPrice(){
        String strPrice = parsePrice(
                document.select("[data-title=\"Contractual price excl. VAT\"]").text());
        if (strPrice.isEmpty()){
            throw new MissingHtmlElementException();
        }
        return new BigDecimal(strPrice);
    }

    /*
        Saves participant companies as well as their offers to provided procurement
     */
    private void saveParticipants(Procurement procurement) throws IOException{
        Elements participants = document.select("[title=\"List of participants\"] .gov-table__row");
        for (Element participantRow : participants){
            //only the addition to base url
            String companyDetailHref = participantRow.select("a").attr("href");

            Company participant = companyDetailScrapper.scrape(companyDetailHref);
            String strPrice = parsePrice(
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

    private String parsePrice(String strPrice){
        return strPrice.replaceAll("\\s", "").replace(',', '.');
    }
}
