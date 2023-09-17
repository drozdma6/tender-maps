package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents company data transfer object
 */
public class CompanyDto{
    private final Long id;
    private final String name;
    private final AddressDto address;
    private final String organisationId;

    public CompanyDto(Long id, String name, AddressDto address, String organisationId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.organisationId = organisationId;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public AddressDto getAddress() {
        return address;
    }

    public String getOrganisationId(){
        return organisationId;
    }
}
