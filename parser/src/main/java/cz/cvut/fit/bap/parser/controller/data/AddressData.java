package cz.cvut.fit.bap.parser.controller.data;

/**
 * Represents address from nen.nipez.com, containing either country full name or country shortcut
 */
public record AddressData(
        String country,
        String city,
        String postalCode,
        String street,
        String buildingNumber,
        String landRegistryNumber,
        String countryCode
){
}