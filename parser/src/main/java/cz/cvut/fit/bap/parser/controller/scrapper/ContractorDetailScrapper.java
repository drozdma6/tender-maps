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
     * Gets contractor authority name
     *
     * @return contractor authority name
     */
    public String getContractorAuthorityName(){
        return document.select("[title=\"Official name\"] p").text();
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
