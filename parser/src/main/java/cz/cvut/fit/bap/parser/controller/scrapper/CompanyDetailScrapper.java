package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import org.jsoup.nodes.Document;

import java.util.Objects;

/**
 * Class for scrapping Company detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/p:pzp:query=finan/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206/vysledek/detail-uverejneni/1596619559">Company detail page</a>
 */
public class CompanyDetailScrapper extends AbstractScrapper{

    public CompanyDetailScrapper(Document document){
        super(document);
    }

    /**
     * Scrapes and saves company's address from company detail page
     *
     * @return scraped address
     */
    public AddressData getCompanyAddress(){
        String city = getNullIfEmpty(document.select("[title=\"Municipality\"] p").text());
        String street = getNullIfEmpty(document.select("[title=\"street\"] p").text());
        String postalCode = getNullIfEmpty(document.select("[title=\"postal code\"] p").text());
        String countryOfficialName = getNullIfEmpty(document.select("[title=\"State\"] p").text());
        String buildingNumber = getNullIfEmpty(
                document.select("[title=\"building number\"] p").text());
        if(Objects.equals(countryOfficialName.toLowerCase(), "česká republika")){
            return new AddressData("CZ", city, postalCode, street, buildingNumber);
        }
        return new AddressData(countryOfficialName, city, postalCode, street, buildingNumber);
    }

    public String getOrganisationId(){
        return getNullIfEmpty(document.select("[title=\"Organisation ID number\"] p").text());
    }
}
