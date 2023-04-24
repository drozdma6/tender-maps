package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import cz.cvut.fit.bap.parser.scrapper.ContractorCompletedScrapper;
import cz.cvut.fit.bap.parser.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import cz.cvut.fit.bap.parser.scrapper.factories.ContractorCompletedFactory;
import cz.cvut.fit.bap.parser.scrapper.factories.ContractorDetailFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/*
    Controller for communication with contractor authority service as well as scrappers
 */
@Component
public class ContractorAuthorityController extends AbstractController<ContractorAuthorityService>{
    private final ContractorDetailFactory contractorDetailFactory;
    private final ContractorCompletedFactory contractorCompletedFactory;
    private final AddressController addressController;

    public ContractorAuthorityController(ContractorAuthorityService contractorAuthorityService,
                                         ContractorDetailFactory contractorDetailFactory,
                                         ContractorCompletedFactory contractorCompletedFactory,
                                         AddressController addressController){
        super(contractorAuthorityService);
        this.contractorDetailFactory = contractorDetailFactory;
        this.contractorCompletedFactory = contractorCompletedFactory;
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

    /**
     * Get completed procurement system numbers of given contractor authority
     *
     * @param contractorAuthority contractor authority
     * @return list of string containing completed procurement system numbers
     */
    public List<String> getProcurementSystemNumbers(ContractorAuthority contractorAuthority){
        ContractorCompletedScrapper contractorCompletedScrapper = contractorCompletedFactory.create();
        return contractorCompletedScrapper.getProcurementSystemNumbers(contractorAuthority);
    }
}