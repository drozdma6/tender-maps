package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.fetcher.AbstractFetcher;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementDetailScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.ProcurementResultScrapper;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementDetailFactory;
import cz.cvut.fit.bap.parser.controller.scrapper.factories.ProcurementResultFactory;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void saveProcurementExisting(){
        ContractorAuthority contractorAuthority = new ContractorAuthority();
        String existingSystemNumber = "existingSystemNumber";
        when(service.existsBySystemNumber(existingSystemNumber)).thenReturn(true);
        boolean res = procurementController.saveProcurement(contractorAuthority, existingSystemNumber);
        assertFalse(res);
    }

    @Test
    void saveProcurementNonExisting(){
        ContractorAuthority contractorAuthority = new ContractorAuthority();
        String systemNumber = "systemNumber";
        Document document = new Document("testDoc");


        ProcurementResultScrapper procurementResultScrapper = mock(ProcurementResultScrapper.class);
        ProcurementDetailScrapper procurementDetailScrapper = mock(ProcurementDetailScrapper.class);

        when(procurementDetailFactory.create(document)).thenReturn(procurementDetailScrapper);

        when(abstractFetcher.getProcurementResult(systemNumber)).thenReturn(document);
        when(procurementResultFactory.create(document)).thenReturn(procurementResultScrapper);

        when(service.existsBySystemNumber(systemNumber)).thenReturn(false);
        when(abstractFetcher.getProcurementDetail(any())).thenReturn(CompletableFuture.completedFuture(document));

        boolean res = procurementController.saveProcurement(contractorAuthority, systemNumber);
        verify(service).existsBySystemNumber(systemNumber);
        assertTrue(res);
        verify(abstractFetcher).getProcurementDetail(systemNumber);
        verify(procurementDetailFactory).create(any(Document.class));
        verify(procurementResultFactory).create(any(Document.class));
    }
}