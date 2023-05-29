package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ProcurementJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceTest{
    @InjectMocks
    private ProcurementService procurementService;

    @Mock
    private ProcurementJpaRepository procurementJpaRepository;

    @Test
    void existsByServiceNumber(){
        String systemNumber = "testSystemNumber";
        when(procurementJpaRepository.existsProcurementBySystemNumber(systemNumber)).thenReturn(
                true);
        assertTrue(procurementService.existsBySystemNumber(systemNumber));
    }

    @Test
    void existsByServiceNumberNonExisting(){
        String systemNumber = "testSystemNumber";
        when(procurementJpaRepository.existsProcurementBySystemNumber(systemNumber)).thenReturn(
                false);
        assertFalse(procurementService.existsBySystemNumber(systemNumber));
    }
}