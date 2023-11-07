package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.AuthorityDetailPageData;
import org.jsoup.nodes.Document;

/**
 * Class for scrapping contracting authority detail page.
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr">Contracting authority detail page</a>
 */
public class AuthorityDetailScrapper extends AbstractScrapper<AuthorityDetailPageData> {

    public AuthorityDetailScrapper(Document document) {
        super(document);
    }

    @Override
    public AuthorityDetailPageData getPageData() {
        return new AuthorityDetailPageData(getContractingAuthorityUrl(), getContractingAuthorityAddress());
    }

    /**
     * Gets contracting authority nen.nipez profile url
     *
     * @return contracting authority's nen.nipez profile url or null if information is missing
     */
    private String getContractingAuthorityUrl() {
        return getNullIfEmpty(document.select("[title=\"Contracting authority's NEN profile\"] p").text());
    }

    /**
     * Scrapes contracting authority's address from detail page
     *
     * @return contracting authority's address
     */
    private AddressData getContractingAuthorityAddress() {
        String city = getNullIfEmpty(document.select("[title=\"Municipality\"] p").text());
        String street = getNullIfEmpty(document.select("[title=\"street\"] p").text());
        String postalCode = getNullIfEmpty(document.select("[title=\"postal code\"] p").text());
        String countryCode = getNullIfEmpty(document.select("[title=\"country - code\"] p").text());
        String landRegistryNumber = getNullIfEmpty(document.select("[title=\"Land registry number\"] p").text());
        String buildingNumber = getNullIfEmpty(
                document.select("[title=\"building number\"] p").text());
        return new AddressData(null, city, postalCode, street, buildingNumber, landRegistryNumber, countryCode);
    }
}
