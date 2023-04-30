package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorCompletedScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorListScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.MissingHtmlElementException;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorCompletedFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorListFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import kotlin.Pair;
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
    private final ContractorListFactory contractorListFactory;

    public ContractorAuthorityController(ContractorAuthorityService contractorAuthorityService,
                                         ContractorDetailFactory contractorDetailFactory,
                                         ContractorCompletedFactory contractorCompletedFactory,
                                         AddressController addressController, AbstractFetcher fetcher, ContractorListFactory contractorListFactory){
        super(contractorAuthorityService);
        this.contractorDetailFactory = contractorDetailFactory;
        this.contractorCompletedFactory = contractorCompletedFactory;
        this.addressController = addressController;
        this.fetcher = fetcher;
        this.contractorListFactory = contractorListFactory;
    }

    /**
     * Gets additional information and saves contractor authority
     *
     * @param href    addition to base url of contractor authority
     * @param profile of contractor authority
     * @return saved contractor authority
     */
    public ContractorAuthority saveContractorAuthority(String href, String profile){
        Optional<ContractorAuthority> optionalAuthority = service.readByProfile(profile);
        if(optionalAuthority.isPresent()){
            return optionalAuthority.get();
        }
        Document document = getContractorDetailPage(href);
        ContractorDetailScrapper contractorDetailScrapper = contractorDetailFactory.create(document);
        String name = contractorDetailScrapper.getContractorAuthorityName();
        AddressDto addressDto = contractorDetailScrapper.getContractorAuthorityAddress();
        Address address = addressController.saveAddress(addressDto);
        return service.create(new ContractorAuthority(name, profile, address, href));
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

    /**
     * Gets information about each contracting authority registered on nen.nipez.cz
     *
     * @return list of pairs, first is link second is profile name
     */
    public List<Pair<String,String>> getContractorAuthorityList(){
        //store link and profile name of authority
        List<Pair<String,String>> authorityHrefList = new ArrayList<>();
        List<Pair<String,String>> pageAuthorityHrefList;
        int page = 1;
        do{
            Document document = getContractorAuthorityList(page++);
            ContractorListScrapper contractorListScrapper = contractorListFactory.create(document);
            pageAuthorityHrefList = contractorListScrapper.getAuthoritiesHrefs();
        }while(authorityHrefList.addAll(pageAuthorityHrefList));
        return authorityHrefList;
    }

    private Document getContractorDetailPage(String href){
        return fetcher.getContractorDetail(href);
    }

    private Document getContractorCompletedPage(ContractorAuthority authority, int page){
        return fetcher.getContractorCompleted(authority.getLink(), page);
    }

    private Document getContractorAuthorityList(int page){
        return fetcher.getContractorAuthorityList(page);
    }
}