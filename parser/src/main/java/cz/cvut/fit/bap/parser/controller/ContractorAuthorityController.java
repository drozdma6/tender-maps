package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorDetailFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
    Controller for contractor authorities
 */
@Component
public class ContractorAuthorityController extends AbstractController<ContractorAuthorityService,ContractorAuthority,Long>{
    private final ContractorDetailFactory contractorDetailFactory;
    private final AddressController addressController;
    private final AbstractFetcher fetcher;

    public ContractorAuthorityController(ContractorAuthorityService contractorAuthorityService,
                                         ContractorDetailFactory contractorDetailFactory,
                                         AddressController addressController, AbstractFetcher fetcher){
        super(contractorAuthorityService);
        this.contractorDetailFactory = contractorDetailFactory;
        this.addressController = addressController;
        this.fetcher = fetcher;
    }

    /**
     * Saves contractor authority
     *
     * @param contractorAuthority which is supposed to be saved
     * @return saved contractor authority
     */
    @Override
    public ContractorAuthority save(ContractorAuthority contractorAuthority){
        //contractor authority was already saved
        if(contractorAuthority.getId() != null){
            return contractorAuthority;
        }
        Address savedAddress = addressController.save(contractorAuthority.getAddress());
        contractorAuthority.setAddress(savedAddress);
        return super.save(contractorAuthority);
    }

    /**
     * Gets contractorAuthority from scrappers.
     *
     * @param contractorDto of contractor authority
     * @return saved contractor authority
     */
    public ContractorAuthority getContractorAuthority(ContractorAuthorityDto contractorDto){
        Optional<ContractorAuthority> optionalAuthority = service.readByName(contractorDto.name());
        if(optionalAuthority.isPresent()){
            return optionalAuthority.get();
        }
        Document document = fetcher.getContractorDetail(contractorDto.url());
        ContractorDetailScrapper contractorDetailScrapper = contractorDetailFactory.create(document);
        String urlToProfile = contractorDetailScrapper.getContractorAuthorityUrl();
        Address geocodedAddress = addressController.geocode(contractorDetailScrapper.getContractorAuthorityAddress());
        return new ContractorAuthority(contractorDto.name(), geocodedAddress, urlToProfile);
    }
}