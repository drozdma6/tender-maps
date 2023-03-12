package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Class for scrapping Company detail page
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/p:pzp:query=finan/detail-profilu/mfcr/uzavrene-zakazky/detail-zakazky/N006-23-V00004206/vysledek/detail-uverejneni/1596619559">Company detail page</a>
 */
@Component
public class CompanyDetailScrapper{
    private final IFetcher fetcher;
    private final CompanyService companyService;
    private final AddressService addressService;
    private Document document;

    public CompanyDetailScrapper(IFetcher fetcher, CompanyService companyService,
                                 AddressService addressService){
        this.fetcher = fetcher;
        this.companyService = companyService;
        this.addressService = addressService;
    }

    /**
     * Scrape company detail page and save company
     *
     * @param url of company detail page
     * @return newly saved company or company which was already saved before
     * @throws IOException if wrong url was provided
     */
    public Company scrape(String url) throws IOException{
        document = fetcher.getCompanyDetail(url);
        String officialName = document.select("[title=\"Official name\"] p").text();
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
        String city = document.select("[title=\"Municipality\"] p").text();
        String street = document.select("[title=\"street\"] p").text();
        String postalCode = document.select("[title=\"postal code\"] p").text();
        String countryOfficialName = document.select("[title=\"State\"] p").text();
        String buildingNumber = document.select("[title=\"building number\"] p").text();

        return addressService.create(
                new Address(countryOfficialName, city, postalCode, street, buildingNumber));
    }
}
