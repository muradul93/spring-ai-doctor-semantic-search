package com.murad.mongo_semantic_search.controller;

import com.murad.mongo_semantic_search.model.DoctorSearchResult;
import com.murad.mongo_semantic_search.service.DoctorSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorSearchController.class)
class DoctorSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorSearchService service;

    @Test
    void searchReturnsRankedDoctorMatches() throws Exception {
        given(service.searchDocuments("chest pain", 5, 0.7, "cardiology"))
            .willReturn(List.of(new DoctorSearchResult(
                "doc-1",
                "Cardiologist in Dhaka",
                Map.of("specialty", "cardiology"),
                0.91
            )));

        mockMvc.perform(get("/api/v1/doctors/search")
                .param("query", "chest pain")
                .param("specialty", "cardiology")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("doc-1"))
            .andExpect(jsonPath("$[0].score").value(0.91))
            .andExpect(jsonPath("$[0].metadata.specialty").value("cardiology"));
    }

    @Test
    void searchRejectsInvalidBounds() throws Exception {
        mockMvc.perform(get("/api/v1/doctors/search")
                .param("query", "chest pain")
                .param("topK", "0")
                .param("similarityThreshold", "1.5"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addDocumentsRejectsBlankContent() throws Exception {
        mockMvc.perform(post("/api/v1/doctors/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"content\":\"   \",\"metadata\":{}}]"))
            .andExpect(status().isBadRequest());
    }
}
