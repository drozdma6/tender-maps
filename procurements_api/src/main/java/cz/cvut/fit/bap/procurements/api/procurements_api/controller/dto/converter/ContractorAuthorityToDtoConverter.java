package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractorAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Domain entity to dto converter
 */
@Component
public class ContractorAuthorityToDtoConverter implements Function<ContractorAuthority, ContractorAuthorityDto> {
    private final AddressToDtoConverter addressToDtoConverter;

    public ContractorAuthorityToDtoConverter(AddressToDtoConverter addressToDtoConverter) {
        this.addressToDtoConverter = addressToDtoConverter;
    }

    /**
     * Converts contractor authority to dto
     *
     * @param contractorAuthority which is supposed to be converted
     * @return contractorAuthority dto
     */
    @Override
    public ContractorAuthorityDto apply(ContractorAuthority contractorAuthority) {
        return new ContractorAuthorityDto(contractorAuthority.getId(), contractorAuthority.getName(),
                addressToDtoConverter.apply(contractorAuthority.getAddress()), contractorAuthority.getUrl());
    }
}
