package cz.cvut.fit.bap.parser.integrated;

import cz.cvut.fit.bap.parser.business.CompanyService;
import cz.cvut.fit.bap.parser.business.ContractingAuthorityService;
import cz.cvut.fit.bap.parser.business.ProcurementService;
import cz.cvut.fit.bap.parser.controller.ProcurementController;
import cz.cvut.fit.bap.parser.domain.Company;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest(properties = {"core.pool.size=5"})
public class ProcurementSaveIntTest{
    @Autowired
    private ProcurementController procurementController;

    @Autowired
    private ProcurementService procurementService;

    @Autowired
    private ContractingAuthorityService contractingAuthorityService;

    @Autowired
    private CompanyService companyService;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void procurementSaveIntegrationTest() {
        String systemNumber = "N006/23/V00006411";
        Assertions.assertFalse(procurementService.existsBySystemNumber(systemNumber));
        procurementController.save(systemNumber);

        Assertions.assertTrue(procurementService.existsBySystemNumber(systemNumber));
        Assertions.assertTrue(contractingAuthorityService.readByName("Městské divadlo Zlín, příspěvková organizace").isPresent());
        Company company = companyService.readByName("SEDASPORT, s.r.o.").get();
        Assertions.assertEquals("SEDASPORT, s.r.o.", company.getName());
        Assertions.assertEquals("Myjava", company.getAddress().getCity());
        Assertions.assertEquals("14", company.getAddress().getBuildingNumber());
        Assertions.assertEquals("907 01", company.getAddress().getPostalCode());
        Assertions.assertEquals("Staromyjavská", company.getAddress().getStreet());
        Assertions.assertNotNull(company.getAddress().getLongitude());
        Assertions.assertNotNull(company.getAddress().getLatitude());
    }
}
