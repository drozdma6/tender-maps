package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractingAuthorityService;
import cz.cvut.fit.bap.parser.controller.dto.ContractingAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.AuthorityDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.AuthorityDetailFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
    Controller for contracting authorities
 */
@Component
public class ContractingAuthorityController extends AbstractController<ContractingAuthorityService, ContractingAuthority, Long> {
    private final AuthorityDetailFactory authorityDetailFactory;
    private final AddressController addressController;
    private final AbstractFetcher fetcher;

    public ContractingAuthorityController(ContractingAuthorityService contractingAuthorityService,
                                          AuthorityDetailFactory authorityDetailFactory,
                                          AddressController addressController, AbstractFetcher fetcher) {
        super(contractingAuthorityService);
        this.authorityDetailFactory = authorityDetailFactory;
        this.addressController = addressController;
        this.fetcher = fetcher;
    }

    /**
     * Saves contracting authority
     *
     * @param contractingAuthority which is supposed to be saved
     * @return saved contracting authority
     */
    @Override
    public ContractingAuthority save(ContractingAuthority contractingAuthority) {
        //contracting authority was already saved
        if (contractingAuthority.getId() != null) {
            return contractingAuthority;
        }
        Address savedAddress = addressController.save(contractingAuthority.getAddress());
        contractingAuthority.setAddress(savedAddress);
        return super.save(contractingAuthority);
    }

    /**
     * Gets contractingAuthority from scrappers.
     *
     * @param contractingAuthorityDto scrapped dto
     * @return saved contracting authority
     */
    public ContractingAuthority getContractingAuthority(ContractingAuthorityDto contractingAuthorityDto) {
        Optional<ContractingAuthority> optionalAuthority = service.readByName(contractingAuthorityDto.name());
        if(optionalAuthority.isPresent()){
            return optionalAuthority.get();
        }
        Document document = fetcher.getAuthorityDetail(contractingAuthorityDto.url());
        AuthorityDetailScrapper authorityDetailScrapper = authorityDetailFactory.create(document);
        String urlToProfile = authorityDetailScrapper.getContractingAuthorityUrl();
        Address geocodedAddress = addressController.geocode(authorityDetailScrapper.getContractingAuthorityAddress());
        return new ContractingAuthority(contractingAuthorityDto.name(), geocodedAddress, urlToProfile);
    }
}