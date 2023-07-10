package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents address dto
 */
public class AddressDto{
    private Long id;
    private String buildingNumber;
    private String city;
    private String street;
    private String countryCode;
    private String postalCode;
    private Double latitude;
    private Double longitude;

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

    public void setId(Long id){
        this.id = id;
    }

    public String getBuildingNumber(){
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber){
        this.buildingNumber = buildingNumber;
    }

    public String getCity(){
        return city;
    }

    public void setCity(String city){
        this.city = city;
    }

    public String getStreet(){
        return street;
    }

    public void setStreet(String street){
        this.street = street;
    }

    public String getCountryCode(){
        return countryCode;
    }

    public void setCountryCode(String countryCode){
        this.countryCode = countryCode;
    }

    public String getPostalCode(){
        return postalCode;
    }

    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }

    public Double getLatitude(){
        return latitude;
    }

    public void setLatitude(Double latitude){
        this.latitude = latitude;
    }

    public Double getLongitude(){
        return longitude;
    }

    public void setLongitude(Double longitude){
        this.longitude = longitude;
    }
}
