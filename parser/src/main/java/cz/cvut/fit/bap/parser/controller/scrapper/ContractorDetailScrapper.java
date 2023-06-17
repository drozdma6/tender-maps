package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import org.jsoup.nodes.Document;

/**
 * Class for scrapping contractor authority detail page.
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr">Contractor authority detail page</a>
 */
public class ContractorDetailScrapper extends AbstractScrapper{

    public ContractorDetailScrapper(Document document){
        super(document);
    }

    /**
     * Gets contractor authority nen.nipez profile url
     *
     * @return contractor authority's nen.nipez profile url or null if information is missing
     */
    public String getContractorAuthorityUrl(){
        return getNullIfEmpty(document.select("[title=\"Contracting authority's NEN profile\"] a").text());
    }

    /**
     * Scrapes contractor authority's address from detail page
     *
     * @return contractor authority's address
     */
    public AddressDto getContractorAuthorityAddress(){
        String city = getNullIfEmpty(document.select("[title=\"Municipality\"] p").text());
        String street = getNullIfEmpty(document.select("[title=\"street\"] p").text());
        String postalCode = getNullIfEmpty(document.select("[title=\"postal code\"] p").text());
        String countryCode = getNullIfEmpty(document.select("[title=\"country - code\"] p").text());
        String buildingNumber = getNullIfEmpty(
                document.select("[title=\"building number\"] p").text());
        return new AddressDto(countryCode, city, postalCode, street, buildingNumber);
    }
}
