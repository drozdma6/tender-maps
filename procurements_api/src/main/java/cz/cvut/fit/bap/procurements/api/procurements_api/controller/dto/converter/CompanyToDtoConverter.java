package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.CompanyDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Company;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CompanyToDtoConverter implements Function<Company,CompanyDto>{
    private final AddressToDtoConverter addressToDtoConverter;

    public CompanyToDtoConverter(AddressToDtoConverter addressToDtoConverter){
        this.addressToDtoConverter = addressToDtoConverter;
    }

    @Override
    public CompanyDto apply(Company company){
        return new CompanyDto(company.getId(), company.getName(), addressToDtoConverter.apply(company.getAddress()), company.getOrganisationId());
    }
}
