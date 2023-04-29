package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorCompletedScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorCompletedFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorDetailFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final AbstractFetcher fetcher;

    public ContractorAuthorityController(ContractorAuthorityService contractorAuthorityService,
                                         ContractorDetailFactory contractorDetailFactory,
                                         ContractorCompletedFactory contractorCompletedFactory,
                                         AddressController addressController, AbstractFetcher fetcher){
        super(contractorAuthorityService);
        this.contractorDetailFactory = contractorDetailFactory;
        this.contractorCompletedFactory = contractorCompletedFactory;
        this.addressController = addressController;
        this.fetcher = fetcher;
    }

    /**
     * Gets additional information and saves contractor authority
     *
     * @param profile of contractor authority
     * @return saved contractor authority
     */
    public ContractorAuthority saveContractorAuthority(String profile){
        Optional<ContractorAuthority> optionalAuthority = service.readByProfile(profile);
        if(optionalAuthority.isPresent()){
            return optionalAuthority.get();
        }
        Document document = getContractorDetailPage(profile);
        ContractorDetailScrapper contractorDetailScrapper = contractorDetailFactory.create(document);
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
        List<String> systemNumberList = new ArrayList<>();
        List<String> pageSystemNumberList = new ArrayList<>();
        int page = 1;
        do{
            Document document = getContractorCompletedPage(contractorAuthority, page++);
            ContractorCompletedScrapper contractorCompletedScrapper = contractorCompletedFactory.create(document);
            try{
                pageSystemNumberList = contractorCompletedScrapper.getProcurementSystemNumbers(contractorAuthority.getName());
                //skip procurement if insufficient data was provided for filtering
            }catch(MissingHtmlElementException ignored){
            }
        }while(systemNumberList.addAll(pageSystemNumberList));
        return systemNumberList;
    }

    private Document getContractorDetailPage(String profile){
        return fetcher.getContractorDetail(profile);
    }

    private Document getContractorCompletedPage(ContractorAuthority authority, int page){
        return fetcher.getContractorCompleted(authority.getProfile(), page);
    }
}