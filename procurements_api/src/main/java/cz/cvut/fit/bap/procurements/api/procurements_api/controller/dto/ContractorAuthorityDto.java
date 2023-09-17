package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents contractor authority data transfer object
 */
public class ContractorAuthorityDto{
    private final Long id;
    private final String name;
    private final AddressDto address;
    private final String url;

    public ContractorAuthorityDto(Long id, String name, AddressDto address, String url) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.url = url;
    }

    public AddressDto getAddress() {
        return address;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }
}
