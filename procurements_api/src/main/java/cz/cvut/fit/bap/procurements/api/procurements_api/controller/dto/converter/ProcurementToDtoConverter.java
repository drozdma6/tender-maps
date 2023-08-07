package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.converter;

import cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto.ProcurementDto;
import cz.cvut.fit.bap.procurements.api.procurements_api.domain.Procurement;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Domain entity to dto converter
 */
@Component
public class ProcurementToDtoConverter implements Function<Procurement, ProcurementDto> {
    private final CompanyToDtoConverter companyToDtoConverter;

    public ProcurementToDtoConverter(CompanyToDtoConverter companyToDtoConverter) {
        this.companyToDtoConverter = companyToDtoConverter;
    }

    /**
     * Converts procurement to dto
     *
     * @param procurement which is supposed to be converted
     * @return procurement dto
     */
    @Override
    public ProcurementDto apply(Procurement procurement) {
        return new ProcurementDto(procurement.getId(), procurement.getName(),
                procurement.getContractPrice(), procurement.getPlaceOfPerformance(),
                procurement.getDateOfPublication(), procurement.getSystemNumber(),
                companyToDtoConverter.apply(procurement.getSupplier()),
                procurement.getContractorAuthority().getName());
    }
}
