package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents address dto
 */
public class AddressDto{
    private final Long id;
    private final String buildingNumber;
    private final String city;
    private final String street;
    private final String countryCode;
    private final String postalCode;
    private final Double latitude;
    private final Double longitude;

    public AddressDto(Long id, String buildingNumber, String city, String street, String countryCode, String postalCode, Double latitude, Double longitude){
        this.id = id;
        this.buildingNumber = buildingNumber;
        this.city = city;
        this.street = street;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId(){
        return id;
    }

    public String getBuildingNumber(){
        return buildingNumber;
    }

    public String getCity(){
        return city;
    }

    public String getStreet(){
        return street;
    }

    public String getCountryCode(){
        return countryCode;
    }

    public String getPostalCode(){
        return postalCode;
    }

    public Double getLatitude(){
        return latitude;
    }

    public Double getLongitude(){
        return longitude;
    }
}
