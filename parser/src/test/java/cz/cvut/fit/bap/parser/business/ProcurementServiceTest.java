package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ProcurementJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProcurementServiceTest{
    @Autowired
    private ProcurementService procurementService;

    @MockBean
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