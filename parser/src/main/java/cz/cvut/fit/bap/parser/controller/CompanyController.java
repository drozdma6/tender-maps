package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
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
    Controller for companies
 */
@Component
public class CompanyController extends AbstractController<CompanyService,Company,Long>{
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
    @Override
    public Company save(Company company){
        Optional<Company> companyOptional = service.readByName(company.getName());
        if(companyOptional.isPresent()){
            return companyOptional.get();
        }
        Address address = addressController.save(company.getAddress());
        return super.save(new Company(company.getName(), address, company.getOrganisationId()));
    }

    /**
     * Get company from url with provided name in separate thread
     *
     * @param url         where information about company is
     * @param companyName of wanted company
     * @return future of scrapped company
     */
    @Async
    public CompletableFuture<Company> getCompanyAsync(String url, String companyName){
        return service.readByName(companyName)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(fetchCompanyDetails(url, companyName)));
    }


    /**
     * Get company from url with provided name in main thread
     *
     * @param url         where information about company is
     * @param companyName of wanted company
     * @return scrapped company
     */
    public Company getCompany(String url, String companyName){
        return service.readByName(companyName)
                .orElseGet(() -> fetchCompanyDetails(url, companyName));
    }

    private Company fetchCompanyDetails(String url, String companyName){
        Document doc = fetcher.getCompanyDetail(url);
        CompanyDetailScrapper companyDetailScrapper = companyDetailFactory.create(doc);
        AddressData addressData = companyDetailScrapper.getCompanyAddress();
        String organisationId = companyDetailScrapper.getOrganisationId();
        Address geocodedAddress = addressController.geocode(addressData);
        return new Company(companyName, geocodedAddress, organisationId);
    }
}
