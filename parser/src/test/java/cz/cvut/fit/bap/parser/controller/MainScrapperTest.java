package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.controller.dto.ContractorAuthorityDto;
import cz.cvut.fit.bap.parser.domain.ContractorAuthority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainScrapperTest{
    @InjectMocks
    private MainScrapper mainScrapper;

    @Mock
    private ContractorAuthorityController contractorAuthorityController;

    @Mock
    private ProcurementController procurementController;

    @Test
    void run(){
        List<ContractorAuthorityDto> authorities1 = List.of(new ContractorAuthorityDto("url1", "name1"),
                new ContractorAuthorityDto("url2", "name2"));
        List<ContractorAuthorityDto> authorities2 = List.of();
        List<String> procurements1 = List.of("systemNumber", "systemNumber2");
        List<String> procurements2 = List.of();


        CompletableFuture<List<ContractorAuthorityDto>> future1 = CompletableFuture.completedFuture(authorities1);
        CompletableFuture<List<ContractorAuthorityDto>> future2 = CompletableFuture.completedFuture(authorities2);
        when(contractorAuthorityController.getNextPageAuthorities())
                .thenReturn(future1)
                .thenReturn(future2);
        when(contractorAuthorityController.saveContractorAuthority(any(ContractorAuthorityDto.class)))
                .thenReturn(new ContractorAuthority());
        when(contractorAuthorityController.getProcurementSystemNumbers(any(ContractorAuthority.class), eq(1)))
                .thenReturn(CompletableFuture.completedFuture(procurements1));
        when(contractorAuthorityController.getProcurementSystemNumbers(any(ContractorAuthority.class), eq(2)))
                .thenReturn(CompletableFuture.completedFuture(procurements2));

        mainScrapper.run();
        verify(contractorAuthorityController, times(2)).getNextPageAuthorities();
        authorities1.forEach(dto ->
                verify(contractorAuthorityController).saveContractorAuthority(dto)
        );
        verify(procurementController, times(2))
                .saveProcurement(any(ContractorAuthority.class), anyString());
        verify(contractorAuthorityController, times(4))
                .getProcurementSystemNumbers(any(ContractorAuthority.class), anyInt());
    }

    @Test
    void runAlreadySavedProcurement(){
        List<ContractorAuthorityDto> authorities1 = List.of(new ContractorAuthorityDto("url1", "name1"));
        List<ContractorAuthorityDto> authorities2 = List.of();
        List<String> procurements1 = List.of("newSystemNumber", "savedSystemNumber", "savedSystemNumber2");
        List<String> procurements2 = List.of();

        CompletableFuture<List<ContractorAuthorityDto>> future1 = CompletableFuture.completedFuture(authorities1);
        CompletableFuture<List<ContractorAuthorityDto>> future2 = CompletableFuture.completedFuture(authorities2);

        when(contractorAuthorityController.getNextPageAuthorities())
                .thenReturn(future1)
                .thenReturn(future2);
        when(contractorAuthorityController.saveContractorAuthority(any(ContractorAuthorityDto.class)))
                .thenReturn(new ContractorAuthority());
        when(contractorAuthorityController.getProcurementSystemNumbers(any(ContractorAuthority.class), eq(1)))
                .thenReturn(CompletableFuture.completedFuture(procurements1));
        when(contractorAuthorityController.getProcurementSystemNumbers(any(ContractorAuthority.class), eq(2)))
                .thenReturn(CompletableFuture.completedFuture(procurements2));
        when(procurementController.saveProcurement(any(), eq(procurements1.get(0)))).thenReturn(true);
        when(procurementController.saveProcurement(any(), eq(procurements1.get(1)))).thenReturn(false);

        mainScrapper.run();

        verify(contractorAuthorityController, times(2)).getNextPageAuthorities();
        authorities1.forEach(dto ->
                verify(contractorAuthorityController).saveContractorAuthority(dto)
        );
        verify(contractorAuthorityController, times(2))
                .getProcurementSystemNumbers(any(ContractorAuthority.class), anyInt());
        verify(procurementController).saveProcurement(any(), eq(procurements1.get(0)));
        verify(procurementController).saveProcurement(any(), eq(procurements1.get(1)));
        verify(procurementController, never()).saveProcurement(any(), eq(procurements1.get(2)));
    }
}
