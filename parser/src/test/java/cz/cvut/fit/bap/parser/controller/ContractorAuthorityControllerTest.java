package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorDetailFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorAuthorityControllerTest{
    @InjectMocks
    private ContractorAuthorityController contractorAuthorityController;

    @Mock
    private ContractorAuthorityService contractorAuthorityService;

    @Mock
    private AddressController addressController;

    @Mock
    private AbstractFetcher fetcher;

    @Mock
    private ContractorDetailFactory contractorDetailFactory;

    @Test
    void getContractorAuthorityExisting(){
        String name = "name";
        String url = "url";
        ContractorAuthority expectedAuthority = new ContractorAuthority(name, new Address(), url);
        ContractorAuthorityDto contractorAuthorityDto = new ContractorAuthorityDto(url, name);

        when(contractorAuthorityService.readByName(name)).thenReturn(Optional.of(expectedAuthority));

        ContractorAuthority actualAuthority = contractorAuthorityController.getContractorAuthority(contractorAuthorityDto);
        verify(fetcher, never()).getContractorDetail(anyString());
        verify(addressController, never()).geocode(any(AddressDto.class));

        Assertions.assertEquals(expectedAuthority.getName(), actualAuthority.getName());
        Assertions.assertEquals(expectedAuthority.getUrl(), actualAuthority.getUrl());

        Assertions.assertEquals(expectedAuthority.getAddress().getCountryCode(), actualAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getCity(), actualAuthority.getAddress().getCity());
        Assertions.assertEquals(expectedAuthority.getAddress().getPostalCode(), actualAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getStreet(), actualAuthority.getAddress().getStreet());
        Assertions.assertEquals(expectedAuthority.getAddress().getBuildingNumber(), actualAuthority.getAddress().getBuildingNumber());
    }

    @Test
    void getContractorAuthorityNonExisting(){
        String name = "name";
        String url = "url";
        Address address = new Address("SK", "Bratislava", "00000", "ulica", "51");
        AddressDto addressDto = new AddressDto("SK", "Bratislava", "00000", "ulica", "51");

        ContractorAuthority expectedAuthority = new ContractorAuthority(name, address, url);
        ContractorAuthorityDto contractorAuthorityDto = new ContractorAuthorityDto(url, name);

        ContractorDetailScrapper contractorDetailScrapper = mock(ContractorDetailScrapper.class);
        Document document = new Document(url);

        when(contractorAuthorityService.readByName(name)).thenReturn(Optional.empty());
        when(fetcher.getContractorDetail(url)).thenReturn(document);
        when(contractorDetailFactory.create(document)).thenReturn(contractorDetailScrapper);
        when(contractorDetailScrapper.getContractorAuthorityAddress()).thenReturn(addressDto);
        when(contractorDetailScrapper.getContractorAuthorityUrl()).thenReturn(url);
        when(addressController.geocode(addressDto)).thenReturn(address);

        ContractorAuthority actualAuthority = contractorAuthorityController.getContractorAuthority(contractorAuthorityDto);

        Assertions.assertEquals(expectedAuthority.getName(), actualAuthority.getName());
        Assertions.assertEquals(expectedAuthority.getUrl(), actualAuthority.getUrl());

        Assertions.assertEquals(expectedAuthority.getAddress().getCountryCode(), actualAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getCity(), actualAuthority.getAddress().getCity());
        Assertions.assertEquals(expectedAuthority.getAddress().getPostalCode(), actualAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getStreet(), actualAuthority.getAddress().getStreet());
        Assertions.assertEquals(expectedAuthority.getAddress().getBuildingNumber(), actualAuthority.getAddress().getBuildingNumber());
    }

    @Test
    void saveContractorAuthorityExisting(){
        ContractorAuthority contractorAuthority = new ContractorAuthority();
        contractorAuthority.setId(1L);
        ContractorAuthority actualAuthority = contractorAuthorityController.save(contractorAuthority);
        Assertions.assertEquals(contractorAuthority, actualAuthority);
    }

    @Test
    void saveContractorAuthorityNonExisting(){
        Address address = new Address();
        ContractorAuthority contractorAuthority = new ContractorAuthority("name", address, "url");
        when(addressController.save(address)).thenReturn(address);
        when(contractorAuthorityService.create(contractorAuthority)).thenReturn(contractorAuthority);

        ContractorAuthority actualAuthority = contractorAuthorityController.save(contractorAuthority);

        Assertions.assertEquals(contractorAuthority, actualAuthority);
    }
}