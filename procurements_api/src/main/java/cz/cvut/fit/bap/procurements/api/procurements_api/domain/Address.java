package cz.cvut.fit.bap.procurements.api.procurements_api.domain;

import jakarta.persistence.*;

import java.util.Objects;

/*
    Class represents address table
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"building_number", "city", "street", "country_code", "postal_code"})})
public class Address implements DomainEntity<Long>{
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "building_number")
    private String buildingNumber;
    @Column
    private String city;
    @Column
    private String street;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "postal_code")
    private String postalCode;
    @Column
    private Double latitude;
    @Column
    private Double longitude;


    public Address(){
    }

    public Address(String countryCode, String city, String postalCode, String street,
                   String buildingNumber){
        this.buildingNumber = buildingNumber;
        this.city = city;
        this.street = street;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
    }

    public Address(String countryCode, String city, String postalCode, String street,
                   String buildingNumber, Double latitude, Double longitude){
        this.buildingNumber = buildingNumber;
        this.city = city;
        this.street = street;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getPostalCode(){
        return postalCode;
    }

    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
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

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Address address))
            return false;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
