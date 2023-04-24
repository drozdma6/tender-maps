package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.scrapper.CompanyDetailScrapper;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import cz.cvut.fit.bap.parser.scrapper.factories.CompanyDetailFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
    Controller for communication with company service
 */
@Component
public class CompanyController extends AbstractController<CompanyService>{
    private final CompanyDetailFactory companyDetailFactory;
    private final AddressController addressController;

    public CompanyController(CompanyDetailFactory companyDetailFactory,
                             CompanyService companyService, AddressController addressController){
        super(companyService);
        this.companyDetailFactory = companyDetailFactory;
        this.addressController = addressController;
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
        if (optionalCompany.isPresent()){
            return optionalCompany.get();
        }
        CompanyDetailScrapper companyDetailScrapper = companyDetailFactory.create(url);
        AddressDto addressDto = companyDetailScrapper.getCompanyAddress();
        return service.create(new Company(companyName, addressController.saveAddress(addressDto)));
    }
}
