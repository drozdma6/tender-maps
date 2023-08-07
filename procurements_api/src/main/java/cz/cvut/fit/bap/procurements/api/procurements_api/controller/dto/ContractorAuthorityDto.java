package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents contractor authority data transfer object
 */
public class ContractorAuthorityDto{
    private Long id;
    private String name;
    private AddressDto address;
    private String url;

    public ContractorAuthorityDto(Long id, String name, AddressDto address, String url) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.url = url;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
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

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
