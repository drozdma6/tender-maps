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
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetSuppliers() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[2].id", Matchers.is(3)))
                .andExpect(jsonPath("$[3].id", Matchers.is(5)));
    }

    @Test
    public void testGetSuppliersNoParams() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetNonSuppliers() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(4)));
    }

    @Test
    public void testGetSuppliers1() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "true")
                        .param("hasExactAddress", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[2].id", Matchers.is(3)));
    }

    @Test
    public void testGetSuppliers2() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "true")
                        .param("placesOfPerformance", "Test Place 3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(3)))
                .andExpect(jsonPath("$[1].id", Matchers.is(5)));
    }

    @Test
    public void testGetSuppliers3() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("contractorAuthorityIds", "1")
                        .param("isSupplier", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(jsonPath("$[2].id", Matchers.is(3)));
    }

    @Test
    public void testGetSuppliers4() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "true")
                        .param("contractorAuthorityIds", "1")
                        .param("placesOfPerformance", "Test Place 1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)));

    }

    @Test
    public void testGetNonSuppliers1() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "false")
                        .param("placesOfPerformance", "Test Place 1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(4)));
    }

    @Test
    public void testGetNonSuppliers2() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "false")
                        .param("placesOfPerformance", "Test Place 3")
                        .param("contractorAuthorityIds", "1")
                        .param("hasExactAddress", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    void testGetNotExactAddressCompanies2() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .param("isSupplier", "true")
                        .param("hasExactAddress", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(5)));
    }

    @Test
    void testGetCompaniesNoParams() throws Exception {
        mockMvc.perform(get("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}