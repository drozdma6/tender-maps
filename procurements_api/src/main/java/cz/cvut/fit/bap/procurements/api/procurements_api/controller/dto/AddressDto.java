package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents address dto
 */
public record AddressDto(
        Long id,
        String buildingNumber,
        String city,
        String street,
        String countryCode,
        String postalCode,
        Double latitude,
        Double longitude) {
}
