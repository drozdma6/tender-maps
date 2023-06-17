package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementListScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementListFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementResultFactory;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcurementControllerTest{
    @InjectMocks
    private ProcurementController procurementController;

    @Mock
    private ProcurementService service;

    @Mock
    private AbstractFetcher abstractFetcher;

    @Mock
    private ProcurementResultFactory procurementResultFactory;

    @Mock
    private ProcurementDetailFactory procurementDetailFactory;

    @Mock
    private ContractorAuthorityController contractorAuthorityController;

    @Mock
    private ProcurementListFactory procurementListFactory;

    @Test
    void getPageSystemNumbers(){
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
    void saveProcurementExisting(){
        String existingSystemNumber = "existingSystemNumber";
        when(service.existsBySystemNumber(existingSystemNumber)).thenReturn(true);
        procurementController.save(existingSystemNumber);
        verify(abstractFetcher, never()).getProcurementDetail(existingSystemNumber);
    }

    @Test
    void saveProcurementNonExisting(){
        String systemNumber = "systemNumber";
        Document document = new Document("testDoc");
        ContractorAuthorityDto contractorAuthorityDto = new ContractorAuthorityDto("url", "name");

        ProcurementResultScrapper procurementResultScrapper = mock(ProcurementResultScrapper.class);
        ProcurementDetailScrapper procurementDetailScrapper = mock(ProcurementDetailScrapper.class);

        when(procurementDetailFactory.create(document)).thenReturn(procurementDetailScrapper);
        when(procurementDetailScrapper.getContractorAuthorityDto()).thenReturn(contractorAuthorityDto);

        when(abstractFetcher.getProcurementResult(systemNumber)).thenReturn(document);
        when(procurementResultFactory.create(document)).thenReturn(procurementResultScrapper);

        when(service.existsBySystemNumber(systemNumber)).thenReturn(false);
        when(abstractFetcher.getProcurementDetail(any())).thenReturn(CompletableFuture.completedFuture(document));

        procurementController.save(systemNumber);
        verify(contractorAuthorityController).getContractorAuthority(contractorAuthorityDto);

        verify(service).existsBySystemNumber(systemNumber);
        verify(abstractFetcher).getProcurementDetail(systemNumber);
        verify(procurementDetailFactory).create(any(Document.class));
        verify(procurementResultFactory).create(any(Document.class));
    }
}