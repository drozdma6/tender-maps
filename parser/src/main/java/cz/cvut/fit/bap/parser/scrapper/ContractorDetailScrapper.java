package cz.cvut.fit.bap.parser.scrapper;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import cz.cvut.fit.bap.parser.scrapper.fetcher.AbstractFetcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Class for scrapping contractor authority detail page.
 *
 * @see <a href="https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/mfcr">Contractor authority detail page</a>
 */
@Component
public class ContractorDetailScrapper extends AbstractScrapper{
    private final ContractorAuthorityService contractorAuthorityService;
    private final AddressService addressService;

    public ContractorDetailScrapper(AbstractFetcher fetcher,
                                    ContractorAuthorityService contractorAuthorityService,
                                    AddressService addressService){
        super(fetcher);
        this.contractorAuthorityService = contractorAuthorityService;
        this.addressService = addressService;
    }

    /**
     * Scrapes contractor detail page
     *
     * @param profile of contractor authority
     * @return saved contractor authority
     * @throws IOException if wrong profile was provided
     */
    public ContractorAuthority scrape(String profile) throws IOException{
        document = fetcher.getContractorDetail(profile);
        String contractorName = document.select("[title=\"Official name\"] p").text();
        Optional<ContractorAuthority> optionalAuthority = contractorAuthorityService.readByName(
                contractorName);
        return optionalAuthority.orElseGet(() -> contractorAuthorityService.create(
                new ContractorAuthority(contractorName, profile, saveAddress())));
    }

    /**
     * Scrapes contractor authority's address from detail page
     *
     * @return contractor authority's address
     */
    private Address saveAddress(){
        String city = getNullIfEmpty(document.select("[title=\"Municipality\"] p").text());
        String street = getNullIfEmpty(document.select("[title=\"street\"] p").text());
        String postalCode = getNullIfEmpty(document.select("[title=\"postal code\"] p").text());
        String countryCode = getNullIfEmpty(document.select("[title=\"country - code\"] p").text());
        String buildingNumber = getNullIfEmpty(
                document.select("[title=\"building number\"] p").text());
        AddressDto addressDto = new AddressDto(countryCode, city, postalCode, street,
                                               buildingNumber);
        return addressService.create(addressDto);
    }
}
