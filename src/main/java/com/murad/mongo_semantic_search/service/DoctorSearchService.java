package com.murad.mongo_semantic_search.service;


import com.murad.mongo_semantic_search.model.DocumentRequest;
import com.murad.mongo_semantic_search.model.DoctorSearchResult;
import com.murad.mongo_semantic_search.repository.DoctorSearchRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service class for handling doctor search operations.
 */
@Service
public class DoctorSearchService {

    private static final int MAX_DOCUMENT_CHARACTERS = 24_000;

    private final DoctorSearchRepository doctorSearchRepository;

    public DoctorSearchService(DoctorSearchRepository doctorSearchRepository) {
        this.doctorSearchRepository = doctorSearchRepository;
    }

    /**
     * Adds validated documents to the repository after filtering out null or excessively long documents.
     *
     * @param documents List of document requests to be added
     * @return List of documents that were successfully added
     */
    public List<Document> addDocuments(List<DocumentRequest> documents) {
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        List<Document> docs = documents.stream()
            .filter(doc -> doc != null && doc.content() != null && !doc.content()
                .trim()
                .isEmpty())
            .filter(doc -> doc.content().length() <= MAX_DOCUMENT_CHARACTERS)
            .map(doc -> new Document(doc.content().trim(), doc.metadata()))
            .toList();

        if (!docs.isEmpty()) {
            doctorSearchRepository.addDocuments(docs);
        }

        return docs;
    }

    /**
     * Deletes documents from the repository based on the provided document IDs.
     *
     * @param ids List of document IDs to be deleted
     * @return List of successfully deleted document IDs, or an empty list if deletion was unsuccessful
     */
    public List<String> deleteDocuments(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList(); // Nothing to delete
        }

        doctorSearchRepository.deleteDocuments(ids);
        return List.copyOf(ids);
    }

    /**
     * Performs a semantic search on documents based on the given query, with specified top results and similarity threshold.
     *
     * @param query The search query
     * @param topK The number of top results to return
     * @param similarityThreshold The minimum similarity score for results to be included
     * @return List of search results containing document content and metadata
     */
    public List<DoctorSearchResult> searchDocuments(
        String query,
        int topK,
        double similarityThreshold,
        String specialty
    ) {
        SearchRequest.Builder requestBuilder = SearchRequest.builder()
            .query(query.trim())
            .topK(topK)
            .similarityThreshold(similarityThreshold);

        if (specialty != null && !specialty.isBlank()) {
            FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
            Expression filterExpression = filterBuilder.eq("specialty", specialty.trim()).build();
            requestBuilder.filterExpression(filterExpression);
        }

        List<Document> results = doctorSearchRepository.semanticSearchByDoctors(requestBuilder.build());

        return results.stream()
            .map(doc -> new DoctorSearchResult(
                doc.getId(),
                doc.getText(),
                doc.getMetadata(),
                doc.getScore()
            ))
            .toList();
    }
}
