package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ContractingAuthorityDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractingAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Domain entity to dto converter
 */
@Component
public class ContractingAuthorityToDtoConverter implements Function<ContractingAuthority, ContractingAuthorityDto> {
    private final AddressToDtoConverter addressToDtoConverter;

    public ContractingAuthorityToDtoConverter(AddressToDtoConverter addressToDtoConverter) {
        this.addressToDtoConverter = addressToDtoConverter;
    }

    /**
     * Converts contracting authority to dto
     *
     * @param contractingAuthority which is supposed to be converted
     * @return contractingAuthority dto
     */
    @Override
    public ContractingAuthorityDto apply(ContractingAuthority contractingAuthority) {
        return new ContractingAuthorityDto(contractingAuthority.getId(), contractingAuthority.getName(),
                addressToDtoConverter.apply(contractingAuthority.getAddress()), contractingAuthority.getUrl());
    }
}
