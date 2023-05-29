package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContractorAuthorityService;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorCompletedScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ContractorListScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorCompletedFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ContractorListFactory;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    private ContractorListFactory contractorListFactory;

    @Mock
    private ContractorCompletedFactory contractorCompletedFactory;

    @Mock
    private ContractorDetailFactory contractorDetailFactory;

    @Test
    void saveContractorAuthorityExisting(){
        String profile = "testProfile";
        Address address = new Address();
        ContractorAuthority contractorAuthority = new ContractorAuthority("testAuthority", profile, address, "testUrl");
        ContractorAuthorityDto contractorAuthorityDto = new ContractorAuthorityDto("testUrl", profile);

        when(contractorAuthorityService.readByProfile(profile)).thenReturn(Optional.of(contractorAuthority));
        ContractorAuthority actualAuthority = contractorAuthorityController.saveContractorAuthority(contractorAuthorityDto);
        verify(fetcher, never()).getContractorDetail(anyString());
        verify(addressController, never()).geocode(any(AddressDto.class));

        Assertions.assertEquals(contractorAuthority.getName(), actualAuthority.getName());
        Assertions.assertEquals(contractorAuthority.getUrl(), actualAuthority.getUrl());
        Assertions.assertEquals(contractorAuthority.getProfile(), actualAuthority.getProfile());

        Assertions.assertEquals(contractorAuthority.getAddress().getCountryCode(), actualAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(contractorAuthority.getAddress().getCity(), actualAuthority.getAddress().getCity());
        Assertions.assertEquals(contractorAuthority.getAddress().getPostalCode(), actualAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(contractorAuthority.getAddress().getStreet(), actualAuthority.getAddress().getStreet());
        Assertions.assertEquals(contractorAuthority.getAddress().getBuildingNumber(), actualAuthority.getAddress().getBuildingNumber());
    }

    @Test
    void saveContractorAuthorityNonExisting(){
        String profile = "testProfile";
        Address address = new Address("SK", "Bratislava", "00000", "ulica", "51");
        AddressDto addressDto = new AddressDto("SK", "Bratislava", "00000", "ulica", "51");
        ContractorAuthority contractorAuthority = new ContractorAuthority("testAuthority", profile, address, "testUrl");
        ContractorAuthorityDto contractorAuthorityDto = new ContractorAuthorityDto("testUrl", profile);
        ContractorDetailScrapper contractorDetailScrapper = mock(ContractorDetailScrapper.class);
        Document document = new Document(contractorAuthority.getUrl());

        when(contractorAuthorityService.readByProfile(profile)).thenReturn(Optional.empty());
        when(fetcher.getContractorDetail(contractorAuthority.getUrl())).thenReturn(document);
        when(contractorDetailFactory.create(document)).thenReturn(contractorDetailScrapper);
        when(contractorDetailScrapper.getContractorAuthorityAddress()).thenReturn(addressDto);
        when(contractorDetailScrapper.getContractorAuthorityName()).thenReturn(contractorAuthority.getName());
        when(addressController.geocode(addressDto)).thenReturn(address);
        when(addressController.saveAddress(address)).thenReturn(address);
        when(contractorAuthorityService.create(contractorAuthority)).thenReturn(contractorAuthority);

        ContractorAuthority actualAuthority = contractorAuthorityController.saveContractorAuthority(contractorAuthorityDto);

        verify(fetcher).getContractorDetail(contractorAuthorityDto.url());
        verify(addressController).geocode(any(AddressDto.class));
        verify(addressController).saveAddress(any(Address.class));
        Assertions.assertEquals(contractorAuthority.getName(), actualAuthority.getName());
        Assertions.assertEquals(contractorAuthority.getUrl(), actualAuthority.getUrl());
        Assertions.assertEquals(contractorAuthority.getProfile(), actualAuthority.getProfile());
        Assertions.assertEquals(contractorAuthority.getAddress().getCountryCode(), actualAuthority.getAddress().getCountryCode());
        Assertions.assertEquals(contractorAuthority.getAddress().getCity(), actualAuthority.getAddress().getCity());
        Assertions.assertEquals(contractorAuthority.getAddress().getPostalCode(), actualAuthority.getAddress().getPostalCode());
        Assertions.assertEquals(contractorAuthority.getAddress().getStreet(), actualAuthority.getAddress().getStreet());
        Assertions.assertEquals(contractorAuthority.getAddress().getBuildingNumber(), actualAuthority.getAddress().getBuildingNumber());
    }

    @Test
    void getNextPageAuthorities(){
        int testAuthorityListPage = 1;
        Document document = mock(Document.class);
        ContractorListScrapper contractorListScrapper = mock(ContractorListScrapper.class);
        List<ContractorAuthorityDto> expectedAuthorities = Arrays.asList(
                new ContractorAuthorityDto("url1", "profile1"),
                new ContractorAuthorityDto("url2", "profile2"));

        when(fetcher.getContractorAuthorityList(testAuthorityListPage)).thenReturn(document);
        when(contractorListFactory.create(document)).thenReturn(contractorListScrapper);
        when(contractorListScrapper.getAuthoritiesHrefs()).thenReturn(expectedAuthorities);

        CompletableFuture<List<ContractorAuthorityDto>> futureResult = contractorAuthorityController.getNextPageAuthorities();
        List<ContractorAuthorityDto> actualAuthorities = futureResult.join();

        verify(fetcher).getContractorAuthorityList(testAuthorityListPage);
        Assertions.assertEquals(expectedAuthorities, actualAuthorities);
    }

    @Test
    void getProcurementSystemNumbers(){
        int testPage = 1;
        Address address = new Address();
        String profile = "testProfile";
        String testUrl = "testUrl";
        Document document = new Document(testUrl);
        ContractorCompletedScrapper contractorCompletedScrapper = mock(ContractorCompletedScrapper.class);
        ContractorAuthority contractorAuthority = new ContractorAuthority("testName", profile, address, testUrl);

        List<String> expectedResult = Arrays.asList("123", "456");

        when(fetcher.getContractorCompleted(testUrl, testPage)).thenReturn(document);
        when(contractorCompletedFactory.create(document)).thenReturn(contractorCompletedScrapper);
        when(contractorCompletedScrapper.getProcurementSystemNumbers(contractorAuthority.getName())).thenReturn(expectedResult);

        List<String> actualResult = contractorAuthorityController.getProcurementSystemNumbers(contractorAuthority, testPage).join();
        verify(fetcher).getContractorCompleted(testUrl, testPage);
        Assertions.assertEquals(expectedResult, actualResult);
    }
}