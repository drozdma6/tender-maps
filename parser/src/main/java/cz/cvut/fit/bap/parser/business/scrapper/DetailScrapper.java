package cz.cvut.fit.bap.parser.business.scrapper;

import cz.cvut.fit.bap.parser.business.CompanyService;
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
public class DetailScrapper{
    private final ProcurementService procurementService;
    private final CompanyService companyService;
    private final OfferService offerService;
    private final IFetcher fetcher;
    private Document document;

    public DetailScrapper(ProcurementService procurementService, CompanyService companyService,
                          OfferService offerService, IFetcher fetcher){
        this.procurementService = procurementService;
        this.companyService = companyService;
        this.offerService = offerService;
        this.fetcher = fetcher;
        this.document = null;
    }

    public void scrapeDetail(String detailUrl, ContractorAuthority authority) throws IOException{
        document = fetcher.getDetail(detailUrl);
        Procurement procurement = saveProcurement(authority);
        saveParticipants(procurement);
    }

    private Procurement saveProcurement(ContractorAuthority authority){
        return procurementService.create(getProcurement(authority));
    }

    /*
        Saves participant companies as well as their offers to provided procurement
     */
    private void saveParticipants(Procurement procurement){
        Elements participants = document.select("[title=\"List of participants\"] .gov-table__row");
        for (Element participantRow : participants){
            String participantName = participantRow.select(
                    ".gov-table__cell--second.gov-table__cell").text();
            Company participant = companyService.create(new Company(participantName));
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

    private String parsePrice(Element element){
        return element.text().replaceAll("\\s", "").replace(',', '.');
    }

    private Procurement getProcurement(ContractorAuthority authority){
        String procurementName = Objects.requireNonNull(document.select("h1").first()).text();

        String strPrice = parsePrice(Objects.requireNonNull(
                document.select("td.gov-table__cell:nth-of-type(6)").first()));
        return new Procurement(procurementName, saveSupplier(), authority,
                               new BigDecimal(strPrice));
    }

    private Company saveSupplier(){
        String supplierName = Objects.requireNonNull(
                document.select("td.gov-table__cell:nth-of-type(4)").first()).text();
        return companyService.create(new Company(supplierName));
    }
}
