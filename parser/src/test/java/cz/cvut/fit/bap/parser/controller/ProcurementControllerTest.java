package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.CurrencyExchanger;
import cz.cvut.fit.bap.parser.controller.data.ContractData;
import cz.cvut.fit.bap.parser.controller.data.ProcurementListPageData;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementListScrapper;
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
    private CurrencyExchanger currencyExchanger;

    @Test
    void getPageSystemNumbers() {
        List<String> expectedSystemNumbers = List.of("systemNumber1", "systemNumber1");
        ProcurementListScrapper procurementListScrapper = mock(ProcurementListScrapper.class);
        ProcurementListPageData procurementListPageData = new ProcurementListPageData(expectedSystemNumbers);
        when(abstractFetcher.getProcurementListScrapper(anyInt())).thenReturn(procurementListScrapper);
        when(procurementListScrapper.getPageData()).thenReturn(procurementListPageData);

        List<String> actualSystemNumbers = procurementController.getPageSystemNumbers(1).join();
        Assertions.assertEquals(expectedSystemNumbers, actualSystemNumbers);
    }

    @Test
    void saveProcurementExisting() {
        String existingSystemNumber = "existingSystemNumber";
        when(service.existsBySystemNumber(existingSystemNumber)).thenReturn(true);
        procurementController.save(existingSystemNumber);
        verify(abstractFetcher, never()).getProcurementDetailScrapper(existingSystemNumber);
    }

    @Test
    void testSumPricesAndFilterByCompanyNameCZK() {
        List<ContractData> offers = getMockContracts(List.of(Currency.CZK, Currency.CZK, Currency.CZK, Currency.CZK));

        List<ContractData> result = procurementController.groupByCompanyAndSum(offers);

        // Assert that we have 2 unique company names in the result
        Assertions.assertEquals(2, result.size());

        // Assert the combined price for CompanyA
        ContractData companyAOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyA"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(40), companyAOffer.price()); //10 + 30
        Assertions.assertEquals("href1", companyAOffer.detailHref());

        // Assert the combined price for CompanyB
        ContractData companyBOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyB"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(30), companyBOffer.price()); // 20 + 10
        Assertions.assertEquals("href2", companyBOffer.detailHref());
    }

    @Test
    void testSumPricesAndFilterByCompanyNameExchange() {
        List<ContractData> offers = getMockContracts(List.of(Currency.CZK, Currency.EUR, Currency.CZK, Currency.EUR));

        when(currencyExchanger.exchange(anyList(), eq(Currency.EUR), eq(Currency.CZK), any())).thenAnswer(invocation -> {
                    List<BigDecimal> args = invocation.getArgument(0);
                    return args.stream()
                            .map(e -> e.multiply(BigDecimal.valueOf(25)))
                            .toList();
                }
        );

        List<ContractData> result = procurementController.groupByCompanyAndSum(offers);

        // Assert that we have 2 unique company names in the result
        Assertions.assertEquals(2, result.size());

        // Assert the combined price for CompanyA
        ContractData companyAOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyA"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(40), companyAOffer.price()); //10 + 30
        Assertions.assertEquals("href1", companyAOffer.detailHref());

        // Assert the combined price for CompanyB
        ContractData companyBOffer = result.stream()
                .filter(o -> o.companyName().equals("CompanyB"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(BigDecimal.valueOf(750), companyBOffer.price()); // 20 + 10
        Assertions.assertEquals("href2", companyBOffer.detailHref());
    }

    private List<ContractData> getMockContracts(List<Currency> currencies) {
        return Arrays.asList(
                new ContractData(BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                        "href1", "CompanyA", currencies.get(0), LocalDate.of(2000, 1, 1)),
                new ContractData(BigDecimal.valueOf(20), BigDecimal.valueOf(20), BigDecimal.valueOf(20), BigDecimal.valueOf(20),
                        "href2", "CompanyB", currencies.get(1), LocalDate.of(2000, 1, 1)),
                new ContractData(BigDecimal.valueOf(30), BigDecimal.valueOf(30), BigDecimal.valueOf(30), BigDecimal.valueOf(30),
                        "href3", "CompanyA", currencies.get(2), LocalDate.of(2000, 1, 1)),
                new ContractData(BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                        "href4", "CompanyB", currencies.get(3), LocalDate.of(2000, 1, 1))
        );

    }
}