package cz.cvut.fit.bap.parser.scrapper.dto;

/**
 * Represents address from nen.nipez.com, containing either country full name or country shortcut
 */
public class AddressDto{
    private String buildingNumber;
    private String city;
    private String street;
    private String postalCode;
    private String countryName; //country full official name
    private String countryCode; //country 2 letter shortcut

    public AddressDto(String country, String city, String postalCode, String street,
                      String buildingNumber){
        //If country is in shortcut format store it in countryCode else store in country name
        if (country.length() == 2){
            countryCode = country;
        } else{
            countryName = country;
        }
        this.buildingNumber = buildingNumber;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
    }

    /**
     * Gets country
     *
     * @return either country official name or country code
     */
    public String getCountry(){
        return countryName == null ? countryCode : countryName;
    }

    public String getCountryCode(){
        return countryCode;
    }

    public void setCountryCode(String countryCode){
        this.countryCode = countryCode;
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

    public String getPostalCode(){
        return postalCode;
    }

    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }

    public String getCountryName(){
        return countryName;
    }

    public void setCountryName(String countryName){
        this.countryName = countryName;
    }
}
