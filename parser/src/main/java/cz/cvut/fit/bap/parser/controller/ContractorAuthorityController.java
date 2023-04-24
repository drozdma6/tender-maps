package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import cz.cvut.fit.bap.parser.scrapper.factories.ContractorDetailFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
    Controller for communication with contractor authority service as well as scrappers
 */
@Component
public class ContractorAuthorityController extends AbstractController<ContractorAuthorityService>{
    private final ContractorDetailFactory contractorDetailFactory;
    private final AddressController addressController;

    public ContractorAuthorityController(ContractorAuthorityService contractorAuthorityService,
                                         ContractorDetailFactory contractorDetailFactory,
                                         AddressController addressController){
        super(contractorAuthorityService);
        this.contractorDetailFactory = contractorDetailFactory;
        this.addressController = addressController;
    }

    /**
     * Gets additional information and saves contractor authority
     *
     * @param profile of contractor authority
     * @return saved contractor authority
     */
    public ContractorAuthority saveContractorAuthority(String profile){
        Optional<ContractorAuthority> optionalAuthority = service.readByProfile(profile);
        if (optionalAuthority.isPresent()){
            return optionalAuthority.get();
        }
        ContractorDetailScrapper contractorDetailScrapper = contractorDetailFactory.create(profile);
        String name = contractorDetailScrapper.getContractorAuthorityName();
        AddressDto addressDto = contractorDetailScrapper.getContractorAuthorityAddress();
        Address address = addressController.saveAddress(addressDto);
        return service.create(new ContractorAuthority(name, profile, address));
    }
}