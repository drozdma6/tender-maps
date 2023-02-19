package cz.cvut.fit.bap.parser.business.scrapper;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.domain.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

@Component
public class Scrapper{
    private final ContractorAuthorityService contractorAuthorityService;
    private final ProcurementService procurementService;
    private final CompanyService companyService;
    private final OfferService offerService;
    private final String baseUrl = "https://nen.nipez.cz";


    public Scrapper(ContractorAuthorityService contractorAuthorityService,
                    ProcurementService procurementService, CompanyService companyService,
                    OfferService offerService){
        this.contractorAuthorityService = contractorAuthorityService;
        this.procurementService = procurementService;
        this.companyService = companyService;
        this.offerService = offerService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scrape() throws IOException{

        String[] contractorProfiles = {"mfcr"};

        for (String profile : contractorProfiles){
            final String mfcrCompletedUrl =
                    "/en/profily-zadavatelu-platne/detail-profilu/" + profile + "/uzavrene-zakazky";
            final Document completedProcurements = Jsoup.connect(baseUrl + mfcrCompletedUrl).get();
            String contractorName = Objects.requireNonNull(
                            completedProcurements.select("td.gov-table__cell:nth-of-type(5)").first())
                    .text();

            ContractorAuthority contractorAuthority = contractorAuthorityService.create(
                    new ContractorAuthority(contractorName));

            parseRows(completedProcurements.select(
                              ".gov-table.gov-table--tablet-block.gov-sortable-table .gov-table__row"),
                      contractorAuthority);

        }
    }

    public void parseRows(Elements rows, ContractorAuthority contractorAuthority)
            throws IOException{

        int i = 0;
        for (Element procurementRow : rows){
            if (!procurementRow.select("[data-title=\"Status\"]").text().equals("Awarded")){
                continue;
            }
            String linkHref = procurementRow.select("a").attr("href");
            scrapeDetail(baseUrl + linkHref, contractorAuthority);
            i++;
            if (i > 2){
                break;
            }
        }

    }

    public void scrapeDetail(String detailLink, ContractorAuthority contractor) throws IOException{
        final Document procurementDetailDoc = Jsoup.connect(detailLink + "/vysledek").get();
        String supplierName = Objects.requireNonNull(
                procurementDetailDoc.select("td.gov-table__cell:nth-of-type(4)").first()).text();

        Company supplier = companyService.create(new Company(supplierName));
        Procurement procurement = saveProcurement(procurementDetailDoc, contractor, supplier);
        saveParticipants(procurementDetailDoc, procurement);
    }

    public void saveParticipants(Document procurementDetailDoc, Procurement procurement){
        for (Element participantRow : procurementDetailDoc.select(
                "[title=\"List of participants\"] .gov-table__row")){
            String participantName = participantRow.select(
                    ".gov-table__cell--second.gov-table__cell").text();
            Company participant = companyService.create(new Company(participantName));
            String strPrice = parsePrice(Objects.requireNonNull(
                    participantRow.select("[data-title=\"Bid price excl. VAT\"]").first()));

            Offer offer;
            OfferId offerId = new OfferId(procurement.getId(), participant.getId());
            if (!strPrice.isEmpty()){
                offer = new Offer(offerId, new BigDecimal(strPrice), procurement, participant);
            } else{
                offer = new Offer(offerId, procurement, participant);
            }
            offerService.create(offer);
        }
    }

    public Procurement saveProcurement(Document procurementDetailDoc,
                                       ContractorAuthority contractorAuthority, Company supplier){
        String procurementName = Objects.requireNonNull(procurementDetailDoc.select("h1").first())
                .text();
        String strPrice = parsePrice(Objects.requireNonNull(
                procurementDetailDoc.select("td.gov-table__cell:nth-of-type(6)").first()));

        Procurement procurement = new Procurement(procurementName, supplier, contractorAuthority,
                                                  new BigDecimal(strPrice));
        return procurementService.create(procurement);
    }

    public String parsePrice(Element element){
        return element.text().replaceAll("\\s", "").replace(',', '.');
    }
}
