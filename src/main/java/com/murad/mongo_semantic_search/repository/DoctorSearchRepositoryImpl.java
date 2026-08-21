package com.murad.mongo_semantic_search.repository;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorSearchRepositoryImpl implements DoctorSearchRepository {

    private final VectorStore vectorStore;

    public DoctorSearchRepositoryImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void addDocuments(List<Document> docs) {
        vectorStore.add(docs);
    }

    @Override
    public void deleteDocuments(List<String> ids) {
        vectorStore.delete(ids);
    }

    @Override
    public List<Document> semanticSearchByDoctors(SearchRequest searchRequest) {
        return vectorStore.similaritySearch(searchRequest);
    }
}
