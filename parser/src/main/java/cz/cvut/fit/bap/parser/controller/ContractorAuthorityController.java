package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorCompletedScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorListScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorCompletedFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorListFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    private int authorityListPage = 1;

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
     * @param contractorDto of contractor authority
     * @return saved contractor authority
     */
    public ContractorAuthority saveContractorAuthority(ContractorAuthorityDto contractorDto){
        Optional<ContractorAuthority> optionalAuthority = service.readByProfile(contractorDto.profileName());
        if(optionalAuthority.isPresent()){
            return optionalAuthority.get();
        }
        Document document = fetcher.getContractorDetail(contractorDto.url());
        ContractorDetailScrapper contractorDetailScrapper = contractorDetailFactory.create(document);
        String name = contractorDetailScrapper.getContractorAuthorityName();
        Address geocodedAddress = addressController.geocode(contractorDetailScrapper.getContractorAuthorityAddress());
        Address address = addressController.saveAddress(geocodedAddress);
        return service.create(new ContractorAuthority(name, contractorDto.profileName(), address, contractorDto.url()));
    }

    /**
     * Scrapes contracting authorities from next page.
     *
     * @return future of list of contractor authority dto scrapped from next page
     */
    @Async
    public CompletableFuture<List<ContractorAuthorityDto>> getNextPageAuthorities(){
        Document document = fetcher.getContractorAuthorityList(authorityListPage++);
        ContractorListScrapper contractorListScrapper = contractorListFactory.create(document);
        return CompletableFuture.completedFuture(contractorListScrapper.getAuthoritiesHrefs());
    }

    /**
     * Get completed procurement system numbers of given contractor authority on provided page
     *
     * @param contractorAuthority who created procurements
     * @param page                which is supposed to be scrapped.
     * @return future of list of procurement system numbers from provided page
     */
    @Async
    public CompletableFuture<List<String>> getProcurementSystemNumbers(ContractorAuthority contractorAuthority, int page){
        Document document = fetcher.getContractorCompleted(contractorAuthority.getUrl(), page);
        ContractorCompletedScrapper contractorCompletedScrapper = contractorCompletedFactory.create(document);
        return CompletableFuture.completedFuture(contractorCompletedScrapper.getProcurementSystemNumbers(contractorAuthority.getName()));
    }
}