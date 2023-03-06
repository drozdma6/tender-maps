package cz.cvut.fit.bap.parser.scrapper;

import com.google.maps.model.GeocodingResult;
import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.business.GeoLocationService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.GeoLocation;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CompanyDetailScrapper{
    private final IFetcher fetcher;
    private final CompanyService companyService;
    private final AddressService addressService;
    private final GeocodingApiClient geocodingApiClient;
    private final GeoLocationService geoLocationService;
    private Document document;

    public CompanyDetailScrapper(IFetcher fetcher, CompanyService companyService,
                                 AddressService addressService,
                                 GeocodingApiClient geocodingApiClient,
                                 GeoLocationService geoLocationService){
        this.fetcher = fetcher;
        this.companyService = companyService;
        this.addressService = addressService;
        this.geocodingApiClient = geocodingApiClient;
        this.geoLocationService = geoLocationService;
    }

    public Company scrape(String url) throws IOException{
        document = fetcher.getCompanyDetail(url);
        String officialName = document.select("[title=\"Official name\"] p").text();
        return companyService.create(new Company(officialName, getCompanyAddress()));
    }

    private Address getCompanyAddress(){
        String city = document.select("[title=\"Municipality\"] p").text();
        String street = document.select("[title=\"street\"] p").text();
        String postalCode = document.select("[title=\"postal code\"] p").text();
        String countryOfficialName = document.select("[title=\"State\"] p").text();
        String buildingNumber = document.select("[title=\"building number\"] p").text();

        GeocodingResult[] geocodingResults = geocodingApiClient.geocode(buildingNumber, street,
                                                                        city, countryOfficialName,
                                                                        postalCode);
        //Database stores 2 letters country codes not officialName
        String countryCode = geocodingApiClient.getCountryShortName(geocodingResults);

        Address address = addressService.create(
                new Address(countryCode, city, postalCode, street, buildingNumber));

        geoLocationService.create(new GeoLocation(geocodingApiClient.getLat(geocodingResults),
                                                  geocodingApiClient.getLng(geocodingResults),
                                                  address));
        return address;
    }
}
