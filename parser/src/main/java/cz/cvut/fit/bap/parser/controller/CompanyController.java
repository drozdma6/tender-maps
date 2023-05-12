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
     * Gets company from url
     *
     * @param url         of company site
     * @param companyName of company
     * @return company instance
     */
    public Company getCompany(String url, String companyName){
        Optional<Company> companyOptional = service.readByName(companyName);
        return companyOptional.orElseGet(() -> getCompanyFromUrl(url, companyName));
    }

    /**
     * Get future of company from url. Runs this method in separate thread.
     *
     * @param url         of company site
     * @param companyName of company
     * @return future of company instance
     */
    @Async
    public CompletableFuture<Company> getCompanyAsync(String url, String companyName){
        Optional<Company> companyOptional = service.readByName(companyName);
        return companyOptional
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(getCompanyFromUrl(url, companyName)));
    }

    /**
     * Saves company if it is not saved already
     *
     * @param company to be stored
     * @return stored company
     */
    public Company saveCompany(Company company){
        if(company.getId() != null){
            return company;
        }
        return service.create(company);
    }

    private Company getCompanyFromUrl(String url, String companyName){
        Document doc = fetcher.getCompanyDetail(url);
        CompanyDetailScrapper companyDetailScrapper = companyDetailFactory.create(doc);
        AddressDto addressDto = companyDetailScrapper.getCompanyAddress();
        String organisationId = companyDetailScrapper.getOrganisationId();
        Address address = addressController.saveAddress(addressDto);
        return new Company(companyName, address, organisationId);
    }
}
