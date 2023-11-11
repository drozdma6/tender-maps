package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractingAuthorityService;
import cz.cvut.fit.bap.parser.controller.data.AuthorityDetailPageData;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.AuthorityDetailScrapper;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
    Controller for contracting authorities
 */
@Component
public class ContractingAuthorityController extends AbstractController<ContractingAuthorityService, ContractingAuthority, Long> {
    private final AddressController addressController;
    private final AbstractFetcher fetcher;

    public ContractingAuthorityController(ContractingAuthorityService contractingAuthorityService,
                                          AddressController addressController,
                                          AbstractFetcher fetcher) {
        super(contractingAuthorityService);
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
     * @param name of contracting authority
     * @param url  of contracting authority detail
     * @return saved contracting authority
     */
    public ContractingAuthority getContractingAuthority(String name, String url) {
        Optional<ContractingAuthority> optionalAuthority = service.readByName(name);
        if (optionalAuthority.isPresent()) {
            return optionalAuthority.get();
        }
        AuthorityDetailScrapper authorityDetailScrapper = fetcher.getAuthorityDetailScrapper(url);
        AuthorityDetailPageData authorityDetailPageData = authorityDetailScrapper.getPageData();
        Address address = addressController.geocode(authorityDetailPageData.addressData());
        return new ContractingAuthority(name, address, authorityDetailPageData.nenProfileUrl());
    }
}