package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.AddressService;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.scrapper.dto.AddressDto;
import org.springframework.stereotype.Component;

/*
    Controller for communication with address service
 */
@Component
public class AddressController extends AbstractController<AddressService>{
    public AddressController(AddressService addressService){
        super(addressService);
    }

    /**
     * Saves address from addressDto
     *
     * @param addressDto which is supposed to get saved
     * @return saved address
     */
    public Address saveAddress(AddressDto addressDto){
        return service.create(addressDto);
    }
}
