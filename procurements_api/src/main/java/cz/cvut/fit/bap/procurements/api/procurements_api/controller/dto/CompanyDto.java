package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents company data transfer object
 */
public class CompanyDto{
    private Long id;
    private String name;
    private AddressDto address;
    private String organisationId;

    public CompanyDto(Long id, String name, AddressDto address, String organisationId) {
        this.id = id;
        this.name = name;
        this.address = address;
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

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getOrganisationId(){
        return organisationId;
    }

    public void setOrganisationId(String organisationId){
        this.organisationId = organisationId;
    }
}
