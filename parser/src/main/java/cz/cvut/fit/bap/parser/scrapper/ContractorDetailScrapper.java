package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.fetcher.IFetcher;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Class for scrapping contractor authority detail page.
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr">Contractor authority detail page</a>
 */
@Component
public class ContractorDetailScrapper{
    private final IFetcher fetcher;
    private final ContractorAuthorityService contractorAuthorityService;
    private final AddressService addressService;
    private Document document;

    public ContractorDetailScrapper(IFetcher fetcher,
                                    ContractorAuthorityService contractorAuthorityService,
                                    AddressService addressService){
        this.fetcher = fetcher;
        this.contractorAuthorityService = contractorAuthorityService;
        this.addressService = addressService;
    }

    public ContractorAuthority scrape(String profile) throws IOException{
        document = fetcher.getContractorDetail(profile);
        String contractorName = document.select("[title=\"Official name\"] p").text();
        Optional<ContractorAuthority> optionalAuthority = contractorAuthorityService.readByName(
                contractorName);
        return optionalAuthority.orElseGet(() -> contractorAuthorityService.create(
                new ContractorAuthority(contractorName, profile, saveAddress())));
    }

    private Address saveAddress(){
        String city = document.select("[title=\"Municipality\"] p").text();
        String street = document.select("[title=\"street\"] p").text();
        String postalCode = document.select("[title=\"postal code\"] p").text();
        String countryCode = document.select("[title=\"country - code\"] p").text();
        String buildingNumber = document.select("[title=\"building number\"] p").text();
        return addressService.create(
                new Address(countryCode, city, postalCode, street, buildingNumber));
    }
}
