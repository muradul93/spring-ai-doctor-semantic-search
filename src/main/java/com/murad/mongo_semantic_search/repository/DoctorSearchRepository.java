package com.murad.mongo_semantic_search.repository;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;

public interface DoctorSearchRepository {

    void addDocuments(List<Document> docs);

    void deleteDocuments(List<String> ids);

    List<Document> semanticSearchByDoctors(SearchRequest searchRequest);
}
