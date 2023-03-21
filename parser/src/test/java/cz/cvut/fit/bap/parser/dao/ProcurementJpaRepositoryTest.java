package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.Procurement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProcurementJpaRepositoryTest{
    @Autowired
    private ProcurementJpaRepository procurementJpaRepository;

    @Test
    void existBySystemNumber(){
        String systemNumber = "systemNumber";
        Procurement procurement = new Procurement("testName", null, null, null,
                                                  "testPlaceOfPerformance", null, systemNumber);
        assertFalse(procurementJpaRepository.existsProcurementBySystemNumber(systemNumber));
        procurementJpaRepository.save(procurement);
        assertTrue(procurementJpaRepository.existsProcurementBySystemNumber(systemNumber));
    }
}