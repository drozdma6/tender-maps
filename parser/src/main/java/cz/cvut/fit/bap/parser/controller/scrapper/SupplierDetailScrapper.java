package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.SupplierDetailPageData;
import org.jsoup.nodes.Document;

/**
 * Class for scrapping supplier detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206/vysledek/detail-uverejneni/1596619580">Supplier detail page</a>
 */
public class SupplierDetailScrapper extends AbstractScrapper<SupplierDetailPageData> {

    public SupplierDetailScrapper(Document document) {
        super(document);
    }

    /**
     * Gets all data scrapped from supplier detail page
     *
     * @return data from supplier detail page
     */
    @Override
    public SupplierDetailPageData getPageData() {
        return new SupplierDetailPageData(
                getCompanyAddress(),
                getOrganisationId(),
                getVATIdNumber(),
                getIsAssociationOfSuppliers()
        );
    }

    /**
     * Scrapes and saves company's address from company detail page
     *
     * @return scraped address
     */
    private AddressData getCompanyAddress() {
        String city = getNullIfEmpty(document.select("[title=\"Municipality\"] p").text());
        String street = getNullIfEmpty(document.select("[title=\"street\"] p").text());
        String postalCode = getNullIfEmpty(document.select("[title=\"postal code\"] p").text());
        String countryOfficialName = getNullIfEmpty(document.select("[title=\"State\"] p").text());
        String landRegistryNumber = getNullIfEmpty(document.select("[title=\"Land registry number\"] p").text());
        String buildingNumber = getNullIfEmpty(
                document.select("[title=\"building number\"] p").text());
        return new AddressData(countryOfficialName, city, postalCode, street, buildingNumber, landRegistryNumber, null);
    }

    private String getOrganisationId() {
        return getNullIfEmpty(document.select("[title=\"Organisation ID number\"] p").text());
    }

    private String getVATIdNumber() {
        return getNullIfEmpty(document.select("[title=\"VAT ID number\"] p").text());
    }

    private Boolean getIsAssociationOfSuppliers() {
        return convertYesNoToBoolean(
                document.select("div.gov-grid-tile:has(h3:contains(Selected supplier is an association of suppliers)) p").text());
    }
}
