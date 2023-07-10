package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents company dto
 */
public class CompanyDto{
    private Long id;
    private String name;
    private AddressDto addressDto;
    private String organisationId;

    public CompanyDto(Long id, String name, AddressDto addressDto, String organisationId){
        this.id = id;
        this.name = name;
        this.addressDto = addressDto;
        this.organisationId = organisationId;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public AddressDto getAddressDto(){
        return addressDto;
    }

    public void setAddressDto(AddressDto addressDto){
        this.addressDto = addressDto;
    }

    public String getOrganisationId(){
        return organisationId;
    }

    public void setOrganisationId(String organisationId){
        this.organisationId = organisationId;
    }
}
