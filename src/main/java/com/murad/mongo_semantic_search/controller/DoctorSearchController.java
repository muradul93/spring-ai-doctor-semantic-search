package com.murad.mongo_semantic_search.controller;


import com.murad.mongo_semantic_search.model.DocumentRequest;
import com.murad.mongo_semantic_search.model.DoctorSearchResult;
import com.murad.mongo_semantic_search.service.DoctorSearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorSearchController {

    private final DoctorSearchService doctorSearchService;

    public DoctorSearchController(DoctorSearchService doctorSearchService) {
        this.doctorSearchService = doctorSearchService;
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public List<DoctorSearchResult> addDocuments(@Valid @RequestBody List<@Valid DocumentRequest> documents) {
        return doctorSearchService.addDocuments(documents)
            .stream()
            .map(doc -> new DoctorSearchResult(doc.getId(), doc.getText(), doc.getMetadata(), doc.getScore()))
            .toList();
    }

    @DeleteMapping("/documents")
    public List<String> deleteDocuments(@RequestBody List<String> ids) {
        return doctorSearchService.deleteDocuments(ids);
    }

    @GetMapping("/search")
    public List<DoctorSearchResult> searchDocuments(
        @RequestParam @NotBlank String query,
        @RequestParam(defaultValue = "5") @Min(1) @Max(50) int topK,
        @RequestParam(defaultValue = "0.7") @DecimalMin("0.0") @DecimalMax("1.0") double similarityThreshold,
        @RequestParam(required = false) String specialty
    ) {
        return doctorSearchService.searchDocuments(query, topK, similarityThreshold, specialty);
    }
}
