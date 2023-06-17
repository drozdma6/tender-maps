package cz.cvut.fit.bap.parser;

import cz.cvut.fit.bap.parser.controller.ProcurementController;
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
    private ProcurementController procurementController;

    @Test
    void run(){
        List<String> procurements1 = List.of("systemNumber", "systemNumber2");
        List<String> procurements2 = List.of();

        when(procurementController.getPageSystemNumbers(1)).thenReturn(CompletableFuture.completedFuture(procurements1));
        when(procurementController.getPageSystemNumbers(2)).thenReturn(CompletableFuture.completedFuture(procurements2));

        mainScrapper.run();
        verify(procurementController).getPageSystemNumbers(1);
        verify(procurementController).getPageSystemNumbers(2);
        verify(procurementController, never()).getPageSystemNumbers(3);

        verify(procurementController).save(procurements1.get(0));
        verify(procurementController).save(procurements1.get(1));
    }
}
