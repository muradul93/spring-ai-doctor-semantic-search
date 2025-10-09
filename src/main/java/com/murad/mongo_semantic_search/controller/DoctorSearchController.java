package com.murad.mongo_semantic_search.controller;


import com.murad.mongo_semantic_search.model.DocumentRequest;
import com.murad.mongo_semantic_search.service.DoctorSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DoctorSearchController {

    @Autowired
    private DoctorSearchService doctorSearchService;

    @PostMapping("/addDocuments")
    public List<Map<String, Object>> addDocuments(@RequestBody List<DocumentRequest> documents) {
        return doctorSearchService.addDocuments(documents)
            .stream()
            .map(doc -> Map.of("content", doc.getContent(), "metadata", doc.getMetadata()))
            .collect(Collectors.toList());
    }

    @DeleteMapping("/delete")
    public List<String> deleteDocuments(@RequestBody List<String> ids) {
        return doctorSearchService.deleteDocuments(ids);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchDocuments(@RequestParam String query, @RequestParam int topK, @RequestParam double similarityThreshold) {
        return doctorSearchService.searchDocuments(query, topK, similarityThreshold);

    }

    @GetMapping("/searchWithFilter")
    public List<Map<String, Object>> searchDocumentsWithFilter(@RequestParam String query, @RequestParam int topK, @RequestParam double similarityThreshold,
        @RequestParam String artist) {
        return doctorSearchService.searchDocumentsWithFilter(query, topK, similarityThreshold, artist);
    }
}
