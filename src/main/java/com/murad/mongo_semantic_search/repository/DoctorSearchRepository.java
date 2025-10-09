package com.murad.mongo_semantic_search.repository;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;
import java.util.Optional;

public interface DoctorSearchRepository {

    void addDocuments(List<Document> docs);

    Optional<Boolean> deleteDocuments(List<String> ids);

    List<Document> semanticSearchByDoctors(SearchRequest searchRequest);
}