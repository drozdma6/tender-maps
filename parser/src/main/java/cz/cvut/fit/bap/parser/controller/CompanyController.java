package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.CompanyDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.CompanyDetailFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/*
    Controller for communication with company service
 */
@Component
public class CompanyController extends AbstractController<CompanyService>{
    private final CompanyDetailFactory companyDetailFactory;
    private final AddressController addressController;
    private final AbstractFetcher fetcher;

    public CompanyController(CompanyDetailFactory companyDetailFactory,
                             CompanyService companyService, AddressController addressController, AbstractFetcher abstractFetcher){
        super(companyService);
        this.companyDetailFactory = companyDetailFactory;
        this.addressController = addressController;
        this.fetcher = abstractFetcher;
    }

    /**
     * Saves company and its address
     *
     * @param company which is supposed to be stored
     * @return stored company
     */
    public Company saveCompany(Company company){
        Optional<Company> companyOptional = service.readByName(company.getName());
        if(companyOptional.isPresent()){
            return companyOptional.get();
        }
        Address address = addressController.saveAddress(company.getAddress());
        return service.create(new Company(company.getName(), address, company.getOrganisationId()));
    }

    /**
     * Get company from url with provided name
     *
     * @param url         where information about company is
     * @param companyName of wanted company
     * @return future of scrapped company
     */
    @Async
    public CompletableFuture<Company> getCompany(String url, String companyName){
        Optional<Company> companyOptional = service.readByName(companyName);
        if(companyOptional.isPresent()){
            return CompletableFuture.completedFuture(companyOptional.get());
        }
        Document doc = fetcher.getCompanyDetail(url);
        CompanyDetailScrapper companyDetailScrapper = companyDetailFactory.create(doc);
        AddressDto addressDto = companyDetailScrapper.getCompanyAddress();
        String organisationId = companyDetailScrapper.getOrganisationId();
        Address geocodedAddress = addressController.geocode(addressDto);
        return CompletableFuture.completedFuture(new Company(companyName, geocodedAddress, organisationId));
    }
}
