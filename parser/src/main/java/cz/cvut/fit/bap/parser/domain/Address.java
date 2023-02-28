package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(
        columnNames = {"building_number", "city", "street", "country_code", "postal_code"})})
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
}
