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
class ContractingAuthorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void readAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/authorities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Authority 1")))
                .andExpect(jsonPath("$[1].name", Matchers.is("Authority 2")))
                .andExpect(jsonPath("$[0].address.id", Matchers.is(1)))
                .andExpect(jsonPath("$[1].address.id", Matchers.is(2)));
    }

    @Test
    void readOne() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/authorities/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Matchers.is("Authority 1")))
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.address.id", Matchers.is(1)));
    }
}