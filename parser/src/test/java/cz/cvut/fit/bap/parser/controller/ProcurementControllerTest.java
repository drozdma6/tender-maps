package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.CurrencyExchanger;
import cz.cvut.fit.bap.parser.controller.dto.ContractDto;
import cz.cvut.fit.bap.parser.controller.dto.OfferDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementListScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementListFactory;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcurementControllerTest {
    @InjectMocks
    private ProcurementController procurementController;

    @Mock
    private ProcurementService service;

    @Mock
    private AbstractFetcher abstractFetcher;

    @Mock
    private ProcurementListFactory procurementListFactory;

    @Mock
    private CurrencyExchanger currencyExchanger;

    @Test
    void getPageSystemNumbers() {
        List<String> expectedSystemNumbers = List.of("systemNumber1", "systemNumber1");
        Document doc = new Document("url");
        ProcurementListScrapper procurementListScrapper = mock(ProcurementListScrapper.class);
        when(abstractFetcher.getProcurementListPage(anyInt())).thenReturn(doc);
        when(procurementListFactory.create(doc)).thenReturn(procurementListScrapper);
        when(procurementListScrapper.getProcurementSystemNumbers()).thenReturn(expectedSystemNumbers);

        List<String> actualSystemNumbers = procurementController.getPageSystemNumbers(1).join();
        Assertions.assertEquals(expectedSystemNumbers, actualSystemNumbers);
    }

    @Test
    void saveProcurementExisting() {
        String existingSystemNumber = "existingSystemNumber";
        when(service.existsBySystemNumber(existingSystemNumber)).thenReturn(true);
        procurementController.save(existingSystemNumber);
        verify(abstractFetcher, never()).getProcurementDetail(existingSystemNumber);
    }

    @Test
    void testSumPricesAndFilterByCompanyNameCZK() {
        List<ContractDto> offers = Arrays.asList(
                new ContractDto(BigDecimal.valueOf(10), "href1", "CompanyA", Currency.CZK, LocalDate.of(2000, 1, 1)),
                new ContractDto(BigDecimal.valueOf(20), "href2", "CompanyB", Currency.CZK, LocalDate.of(2000, 1, 1)),
                new ContractDto(BigDecimal.valueOf(30), "href3", "CompanyA", Currency.CZK, LocalDate.of(2000, 1, 1)),
                new ContractDto(BigDecimal.valueOf(10), "href4", "CompanyB", Currency.CZK, LocalDate.of(2000, 1, 1))
        );

        List<ContractDto> result = procurementController.sumPricesAndFilterByCompanyName(offers);

        // Assert that we have 2 unique company names in the result
        Assertions.assertEquals(2, result.size());

        // Assert the combined price for CompanyA
        ContractDto companyAOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyA"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(40), companyAOffer.contractPrice()); //10 + 30
        Assertions.assertEquals("href1", companyAOffer.detailHref());

        // Assert the combined price for CompanyB
        ContractDto companyBOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyB"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(30), companyBOffer.contractPrice()); // 20 + 10
        Assertions.assertEquals("href2", companyBOffer.detailHref());
    }

    @Test
    void testSumPricesAndFilterByCompanyNameExchange() {
        List<ContractDto> offers = Arrays.asList(
                new ContractDto(BigDecimal.valueOf(10), "href1", "CompanyA", Currency.CZK, LocalDate.of(2000, 1, 1)),
                new ContractDto(BigDecimal.valueOf(20), "href2", "CompanyB", Currency.EUR, LocalDate.of(2000, 1, 1)),
                new ContractDto(BigDecimal.valueOf(30), "href3", "CompanyA", Currency.CZK, LocalDate.of(2000, 1, 1)),
                new ContractDto(BigDecimal.valueOf(10), "href4", "CompanyB", Currency.EUR, LocalDate.of(2000, 1, 1))
        );

        when(currencyExchanger.exchange(any(), any(), eq(Currency.CZK), any())).thenAnswer(invocation -> {
            BigDecimal argValue = invocation.getArgument(0);
            return Optional.of(argValue.multiply(BigDecimal.valueOf(25)));
        });

        List<ContractDto> result = procurementController.sumPricesAndFilterByCompanyName(offers);

        // Assert that we have 2 unique company names in the result
        Assertions.assertEquals(2, result.size());

        // Assert the combined price for CompanyA
        ContractDto companyAOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyA"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(40), companyAOffer.contractPrice()); //10 + 30
        Assertions.assertEquals("href1", companyAOffer.detailHref());

        // Assert the combined price for CompanyB
        ContractDto companyBOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyB"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(750), companyBOffer.contractPrice()); // 20 + 10
        Assertions.assertEquals("href2", companyBOffer.detailHref());
    }

    @Test
    public void testExchangeCurrenciesToCZK() {
        LocalDate testDate = LocalDate.now();
        OfferDto offerCZK = new OfferDto(BigDecimal.valueOf(100), "someHref1", "someCompany1", Currency.CZK);
        OfferDto offerUSD = new OfferDto(BigDecimal.valueOf(10), "someHref2", "someCompany2", Currency.USD);

        when(currencyExchanger.exchange(BigDecimal.valueOf(10), Currency.USD, Currency.CZK, testDate))
                .thenReturn(Optional.of(BigDecimal.valueOf(250))); // Mocking a conversion rate of 1 USD = 25 CZK

        List<OfferDto> offerDtos = Arrays.asList(offerCZK, offerUSD);

        List<OfferDto> result = procurementController.exchangeCurrenciesToCZK(offerDtos, testDate);

        // Then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(BigDecimal.valueOf(100), result.get(0).price());
        Assertions.assertEquals(Currency.CZK, result.get(0).currency());
        Assertions.assertEquals(BigDecimal.valueOf(250), result.get(1).price());
        Assertions.assertEquals(Currency.CZK, result.get(1).currency());
    }
}