package cz.cvut.fit.bap.parser.business.scrapper;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.business.fetcher.IFetcher;
import cz.cvut.fit.bap.parser.domain.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

@Component
public class ProcurementResultScrapper{
    private final ProcurementService procurementService;
    private final OfferService offerService;
    private final IFetcher fetcher;
    private final CompanyDetailScrapper companyDetailScrapper;
    private final ProcurementDetailScrapper procurementDetailScrapper;
    private Document document;

    public ProcurementResultScrapper(ProcurementService procurementService,
                                     OfferService offerService, IFetcher fetcher,
                                     CompanyDetailScrapper companyDetailScrapper,
                                     ProcurementDetailScrapper procurementDetailScrapper){
        this.procurementService = procurementService;
        this.offerService = offerService;
        this.fetcher = fetcher;
        this.companyDetailScrapper = companyDetailScrapper;
        this.procurementDetailScrapper = procurementDetailScrapper;
        this.document = null;
    }

    public void scrapeProcurementResult(String detailUrl, ContractorAuthority authority)
            throws IOException{
        document = fetcher.getProcurementResult(detailUrl);
        String placeOfPerformance = procurementDetailScrapper.getProcurementPlaceOfPerformance(
                detailUrl);
        Procurement procurement = saveProcurement(authority, placeOfPerformance);
        saveParticipants(procurement);
    }

    /*
        Saves participant companies as well as their offers to provided procurement
     */
    private void saveParticipants(Procurement procurement) throws IOException{
        Elements participants = document.select("[title=\"List of participants\"] .gov-table__row");
        for (Element participantRow : participants){
            //only the addition to base url
            String companyDetailHref = participantRow.select("a").attr("href");

            Company participant = companyDetailScrapper.saveCompany(companyDetailHref);
            String strPrice = parsePrice(Objects.requireNonNull(
                    participantRow.select("[data-title=\"Bid price excl. VAT\"]").first()));
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

    private Procurement saveProcurement(ContractorAuthority authority, String placeOfPerformance)
            throws IOException{
        String procurementName = Objects.requireNonNull(document.select("h1").first()).text();

        String strPrice = parsePrice(Objects.requireNonNull(
                document.select("td.gov-table__cell:nth-of-type(6)").first()));
        Company supplier = saveSupplier();
        return procurementService.create(
                new Procurement(procurementName, supplier, authority, new BigDecimal(strPrice),
                                placeOfPerformance));
    }

    private Company saveSupplier() throws IOException{
        //only the addition to base url
        String companyDetailHref = Objects.requireNonNull(
                document.select("td.gov-table__cell:nth-of-type(1) a").first()).attr("href");
        return companyDetailScrapper.saveCompany(companyDetailHref);
    }

    private String parsePrice(Element element){
        return element.text().replaceAll("\\s", "").replace(',', '.');
    }
}
