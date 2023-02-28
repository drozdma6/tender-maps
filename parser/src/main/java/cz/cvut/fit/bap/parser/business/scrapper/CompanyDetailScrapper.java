package cz.cvut.fit.bap.parser.business.scrapper;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.business.fetcher.IFetcher;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CompanyDetailScrapper{
    private final IFetcher fetcher;
    private final CompanyService companyService;
    private final RestCountriesClient restCountriesClient;
    private final AddressService addressService;

    public CompanyDetailScrapper(IFetcher fetcher, CompanyService companyService,
                                 RestCountriesClient restCountriesClient,
                                 AddressService addressService){
        this.fetcher = fetcher;
        this.companyService = companyService;
        this.restCountriesClient = restCountriesClient;
        this.addressService = addressService;
    }

    public Company saveCompany(String detailUri) throws IOException{
        Document document = fetcher.getCompanyDetail(detailUri);
        String officialName = document.select("[title=\"Official name\"] p").text();
        String city = document.select("[title=\"Municipality\"] p").text();
        String street = document.select("[title=\"street\"] p").text();
        String postalCode = document.select("[title=\"postal code\"] p").text();
        String countryOfficialName = document.select("[title=\"State\"] p").text();
        String buildingNumber = document.select("[title=\"building number\"] p").text();

        //Database stores 2 letter country codes
        String countryCode = restCountriesClient.getCountryShortcut(countryOfficialName);
        Address address = addressService.create(
                new Address(countryCode, city, postalCode, street, buildingNumber));
        return companyService.create(new Company(officialName, address));
    }
}
