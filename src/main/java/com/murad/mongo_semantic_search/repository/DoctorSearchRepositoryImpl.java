package com.murad.mongo_semantic_search.repository;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DoctorSearchRepositoryImpl implements DoctorSearchRepository {

    private final VectorStore vectorStore;

    @Autowired
    public DoctorSearchRepositoryImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void addDocuments(List<Document> docs) {
        vectorStore.add(docs);
    }

    @Override
    public Optional<Boolean> deleteDocuments(List<String> ids) {
        return vectorStore.delete(ids);
    }

    @Override
    public List<Document> semanticSearchByDoctors(SearchRequest searchRequest) {
        return vectorStore.similaritySearch(searchRequest);
    }
}