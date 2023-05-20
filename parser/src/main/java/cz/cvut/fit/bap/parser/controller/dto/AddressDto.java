package cz.cvut.fit.bap.parser.controller.dto;

/**
 * Represents address from nen.nipez.com, containing either country full name or country shortcut
 */
public record AddressDto(
        String country,
        String city,
        String postalCode,
        String street,
        String buildingNumber
){
    public String getCountryCode(){
        return country.length() == 2 ? country : null;
    }
}