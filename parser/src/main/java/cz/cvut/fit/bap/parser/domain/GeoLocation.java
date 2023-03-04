package cz.cvut.fit.bap.parser.domain;

import jakarta.persistence.*;

@Entity
public class GeoLocation implements DomainEntity<Long>{
    @Id
    private Long id;
    @Column
    private Double latitude;
    @Column
    private Double longitude;
    @OneToOne
    @MapsId
    private Address address;

    public GeoLocation(Double latitude, Double longitude, Address address){
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.id = address.getId();
    }

    public GeoLocation(){
    }

    public Address getAddress(){
        return address;
    }

    public void setAddress(Address address){
        this.address = address;
        this.id = address.getId();
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

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public void setId(Long id){
        this.id = id;
    }
}
