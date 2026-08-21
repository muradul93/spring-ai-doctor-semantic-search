package com.murad.mongo_semantic_search.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DoctorSearchRepositoryImplTest {

    @Mock
    private VectorStore vectorStore;

    @Test
    void semanticSearchDelegatesToVectorStore() {
        DoctorSearchRepositoryImpl repository = new DoctorSearchRepositoryImpl(vectorStore);
        SearchRequest request = SearchRequest.builder().query("migraine").topK(3).build();
        List<Document> matches = List.of(new Document("Neurologist", Map.of("specialty", "neurology")));
        given(vectorStore.similaritySearch(request)).willReturn(matches);

        List<Document> result = repository.semanticSearchByDoctors(request);

        assertThat(result).isSameAs(matches);
        then(vectorStore).should().similaritySearch(request);
    }

    @Test
    void deleteDocumentsDelegatesToVectorStore() {
        DoctorSearchRepositoryImpl repository = new DoctorSearchRepositoryImpl(vectorStore);
        List<String> ids = List.of("doctor-1", "doctor-2");

        repository.deleteDocuments(ids);

        then(vectorStore).should().delete(ids);
    }
}
