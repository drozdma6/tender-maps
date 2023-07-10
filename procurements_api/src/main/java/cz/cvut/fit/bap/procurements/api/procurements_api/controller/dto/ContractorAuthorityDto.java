package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents contractor authority dto
 */
public class ContractorAuthorityDto{
    private Long id;
    private String name;
    private Long addressId;
    private String url;

    public ContractorAuthorityDto(Long id, String name, Long addressId, String url){
        this.id = id;
        this.name = name;
        this.addressId = addressId;
        this.url = url;
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

    public Long getAddressId(){
        return addressId;
    }

    public void setAddressId(Long addressId){
        this.addressId = addressId;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
