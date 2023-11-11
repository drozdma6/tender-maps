package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractingAuthorityService;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.AuthorityDetailPageData;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.AuthorityDetailScrapper;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
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
class ContractingAuthorityControllerTest {
    @InjectMocks
    private ContractingAuthorityController contractingAuthorityController;

    @Mock
    private ContractingAuthorityService contractingAuthorityService;

    @Mock
    private AddressController addressController;

    @Mock
    private AbstractFetcher fetcher;

    @Test
    void getContractingAuthorityExisting() {
        String name = "name";
        String url = "url";
        ContractingAuthority expectedAuthority = new ContractingAuthority(name, new Address(), url);

        when(contractingAuthorityService.readByName(name)).thenReturn(Optional.of(expectedAuthority));

        ContractingAuthority actualAuthority = contractingAuthorityController.getContractingAuthority(name, url);
        verify(fetcher, never()).getAuthorityDetailScrapper(anyString());
        verify(addressController, never()).geocode(any(AddressData.class));

        Assertions.assertEquals(expectedAuthority.getName(), actualAuthority.getName());
        Assertions.assertEquals(expectedAuthority.getUrl(), actualAuthority.getUrl());

        Assertions.assertEquals(expectedAuthority.getAddress().getCountryCode(), actualAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getCity(), actualAuthority.getAddress().getCity());
        Assertions.assertEquals(expectedAuthority.getAddress().getPostalCode(), actualAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getStreet(), actualAuthority.getAddress().getStreet());
        Assertions.assertEquals(expectedAuthority.getAddress().getBuildingNumber(), actualAuthority.getAddress().getBuildingNumber());
    }

    @Test
    void getContractingAuthorityNonExisting() {
        String name = "name";
        String url = "url";
        Address address = new Address("SK", "Bratislava", "00000", "ulica", "51", "1", 1.0, 1.0);
        AddressData addressData = new AddressData("SK", "Bratislava", "00000", "ulica", "51", "1", null);
        AuthorityDetailPageData authorityDetailPageData = new AuthorityDetailPageData(url, addressData);
        ContractingAuthority expectedAuthority = new ContractingAuthority(name, address, url);

        AuthorityDetailScrapper authorityDetailScrapper = mock(AuthorityDetailScrapper.class);

        when(contractingAuthorityService.readByName(name)).thenReturn(Optional.empty());
        when(fetcher.getAuthorityDetailScrapper(url)).thenReturn(authorityDetailScrapper);
        when(authorityDetailScrapper.getPageData()).thenReturn(authorityDetailPageData);
        when(addressController.geocode(addressData)).thenReturn(address);

        ContractingAuthority actualAuthority = contractingAuthorityController.getContractingAuthority(name, url);

        Assertions.assertEquals(expectedAuthority.getName(), actualAuthority.getName());
        Assertions.assertEquals(expectedAuthority.getUrl(), actualAuthority.getUrl());

        Assertions.assertEquals(expectedAuthority.getAddress().getCountryCode(), actualAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getCity(), actualAuthority.getAddress().getCity());
        Assertions.assertEquals(expectedAuthority.getAddress().getPostalCode(), actualAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(expectedAuthority.getAddress().getStreet(), actualAuthority.getAddress().getStreet());
        Assertions.assertEquals(expectedAuthority.getAddress().getBuildingNumber(), actualAuthority.getAddress().getBuildingNumber());
    }

    @Test
    void saveContractingAuthorityExisting() {
        ContractingAuthority contractingAuthority = new ContractingAuthority();
        contractingAuthority.setId(1L);
        ContractingAuthority actualAuthority = contractingAuthorityController.save(contractingAuthority);
        Assertions.assertEquals(contractingAuthority, actualAuthority);
    }

    @Test
    void saveContractingAuthorityNonExisting() {
        Address address = new Address();
        ContractingAuthority contractingAuthority = new ContractingAuthority("name", address, "url");
        when(addressController.save(address)).thenReturn(address);
        when(contractingAuthorityService.create(contractingAuthority)).thenReturn(contractingAuthority);

        ContractingAuthority actualAuthority = contractingAuthorityController.save(contractingAuthority);

        Assertions.assertEquals(contractingAuthority, actualAuthority);
    }
}