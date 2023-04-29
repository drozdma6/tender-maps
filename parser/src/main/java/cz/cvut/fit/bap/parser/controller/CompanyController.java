package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.CompanyDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.CompanyDetailFactory;
import cz.cvut.fit.bap.parser.domain.Company;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
     * Gets additional data about company and saves it
     *
     * @param url         of company site
     * @param companyName of company which is supposed to be saved
     * @return saved Company
     */
    public Company saveCompany(String url, String companyName){
        Optional<Company> optionalCompany = service.readByName(companyName);
        if(optionalCompany.isPresent()){
            return optionalCompany.get();
        }
        Document doc = getDocument(url);
        CompanyDetailScrapper companyDetailScrapper = companyDetailFactory.create(doc);
        AddressDto addressDto = companyDetailScrapper.getCompanyAddress();
        return service.create(new Company(companyName, addressController.saveAddress(addressDto)));
    }


    private Document getDocument(String url){
        return fetcher.getCompanyDetail(url);
    }
}
