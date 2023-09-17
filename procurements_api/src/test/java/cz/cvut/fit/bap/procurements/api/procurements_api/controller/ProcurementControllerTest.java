package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProcurementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testReadAllNoParams() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[2].id", Matchers.is(3)))
                .andExpect(jsonPath("$[3].id", Matchers.is(4)));
    }

    @Test
    public void testReadAllParams() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .param("placesOfPerformance", "Test Place 1")
                        .param("contractorAuthorityIds", "1")
                        .param("supplierHasExactAddress", "true")
                        .param("supplierId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Procurement 1")))
                .andExpect(jsonPath("$[0].contractPrice", Matchers.is(50000.00)))
                .andExpect(jsonPath("$[0].systemNumber", Matchers.is("SYS001")));
    }

    @Test
    public void testWithPlaceOfPerformanceAndSupplierId() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .param("placesOfPerformance", "Test Place 3")
                        .param("supplierId", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(3)));
    }

    @Test
    public void testWithPlaceOfPerformances() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .param("placesOfPerformance", "Test Place 1", "Test Place 3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(3)))
                .andExpect(jsonPath("$[2].id", Matchers.is(4)));
    }

    @Test
    public void testWithContractorAuthorityId() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .param("contractorAuthorityIds", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(2)))
                .andExpect(jsonPath("$[1].id", Matchers.is(4)));
    }

    @Test
    public void testWithContractorAuthorityIds() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .param("contractorAuthorityIds", "2", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(3)))
                .andExpect(jsonPath("$[2].id", Matchers.is(2)))
                .andExpect(jsonPath("$[3].id", Matchers.is(4)));
    }

    @Test
    public void testWithSupplierHasExactAddress() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .param("supplierHasExactAddress", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[2].id", Matchers.is(3)));
    }

    @Test
    public void testWithSupplierHasNotExactAddress() throws Exception {
        mockMvc.perform(get("/api/procurements")
                        .param("supplierHasExactAddress", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(4)));
    }
}