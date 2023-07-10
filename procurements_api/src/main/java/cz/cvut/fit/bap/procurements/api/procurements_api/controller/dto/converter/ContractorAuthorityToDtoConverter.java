package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.ContractorAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ContractorAuthorityToDtoConverter implements Function<ContractorAuthority,ContractorAuthorityDto>{
    @Override
    public ContractorAuthorityDto apply(ContractorAuthority contractorAuthority){
        return new ContractorAuthorityDto(contractorAuthority.getId(), contractorAuthority.getName(),
                contractorAuthority.getAddress().getId(), contractorAuthority.getUrl());
    }
}
