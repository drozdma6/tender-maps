package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.SupplierDetailPageData;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.SupplierDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.CompanyDetailFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        return super.save(new Company(company.getName(), address, company.getOrganisationId(), company.getVATIdNumber()));
    }

    /**
     * Builds Company from provided data
     * @param name of company
     * @param addressData of company
     * @param organisationId of company
     * @param VATIdNumber of company
     * @return company with geocoded addressData
     */
    public Company buildCompany(String name, AddressData addressData, String organisationId, String VATIdNumber) {
        Address address = addressController.geocode(addressData);
        return new Company(name, address, organisationId, VATIdNumber);
    }

    /**
     * Gets supplier detail page data from provided url.
     * @param url to supplier detail page
     * @return scrapped data from supplier detail page
     */
    public SupplierDetailPageData getSupplierDetailPageData(String url) {
        Document doc = fetcher.getSupplierDetail(url);
        SupplierDetailScrapper supplierDetailScrapper = companyDetailFactory.create(doc);
        return supplierDetailScrapper.getPageData();
    }
}
