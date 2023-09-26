package cz.cvut.fit.bap.procurements.api.procurements_api.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOffersByCompanyId1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/offers")
                        .param("companyId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)));
    }

    @Test
    void getOffersByCompanyIdNoParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()); //missing required param
    }

    @Test
    void getOffersByCompanyId2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/offers")
                        .param("companyId", "2")
                        .param("placesOfPerformance", "Test Place 2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(2)));
    }

    @Test
    void getOffersByCompanyId3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/offers")
                        .param("companyId", "2")
                        .param("placesOfPerformance", "Test Place 2")
                        .param("contractingAuthorityIds", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
}