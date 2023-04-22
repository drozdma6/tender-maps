package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Class for scrapping Company detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/p:pzp:query=finan/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206/vysledek/detail-uverejneni/1596619559">Company detail page</a>
 */
@Component
public class CompanyDetailScrapper extends AbstractScrapper{
    private final CompanyService companyService;
    private final AddressService addressService;

    public CompanyDetailScrapper(AbstractFetcher fetcher, CompanyService companyService,
                                 AddressService addressService){
        super(fetcher);
        this.companyService = companyService;
        this.addressService = addressService;
    }

    /**
     * Scrape company detail page and save company
     *
     * @param url          of company detail page
     * @param officialName of company
     * @return newly saved company or company which was already saved before
     * @throws IOException if wrong url was provided
     */
    public Company scrape(String url, String officialName) throws IOException{
        document = fetcher.getCompanyDetail(url);
        Optional<Company> optionalCompany = companyService.readByName(officialName);
        return optionalCompany.orElseGet(
                () -> companyService.create(new Company(officialName, saveCompanyAddress())));
    }

    /**
     * Scrapes and saves company's address from company detail page
     *
     * @return scraped address
     */
    private Address saveCompanyAddress(){
        String city = getNullIfEmpty(document.select("[title=\"Municipality\"] p").text());
        String street = getNullIfEmpty(document.select("[title=\"street\"] p").text());
        String postalCode = getNullIfEmpty(document.select("[title=\"postal code\"] p").text());
        String countryOfficialName = getNullIfEmpty(document.select("[title=\"State\"] p").text());
        String buildingNumber = getNullIfEmpty(
                document.select("[title=\"building number\"] p").text());
        AddressDto addressDto = new AddressDto(countryOfficialName, city, postalCode, street,
                                               buildingNumber);
        return addressService.create(addressDto);
    }
}
