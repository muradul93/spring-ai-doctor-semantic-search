package com.murad.mongo_semantic_search.service;

import com.murad.mongo_semantic_search.model.DocumentRequest;
import com.murad.mongo_semantic_search.model.DoctorSearchResult;
import com.murad.mongo_semantic_search.repository.DoctorSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class DoctorSearchServiceTest {

    @Mock
    private DoctorSearchRepository repository;

    @Test
    void addDocumentsIndexesOnlyUsableContent() {
        DoctorSearchService service = new DoctorSearchService(repository);
        List<DocumentRequest> requests = List.of(
            new DocumentRequest("Treats heart rhythm disorders", Map.of("specialty", "cardiology")),
            new DocumentRequest("   ", Map.of()),
            new DocumentRequest(null, Map.of())
        );

        List<Document> indexed = service.addDocuments(requests);

        assertThat(indexed)
            .singleElement()
            .satisfies(document -> {
                assertThat(document.getText()).isEqualTo("Treats heart rhythm disorders");
                assertThat(document.getMetadata()).containsEntry("specialty", "cardiology");
            });
        then(repository).should().addDocuments(indexed);
    }

    @Test
    void addDocumentsDoesNotCallRepositoryWhenNothingIsUsable() {
        DoctorSearchService service = new DoctorSearchService(repository);

        List<Document> indexed = service.addDocuments(List.of(new DocumentRequest(" ", Map.of())));

        assertThat(indexed).isEmpty();
        then(repository).should(never()).addDocuments(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void searchDocumentsBuildsBoundedSemanticQueryWithSpecialtyFilter() {
        DoctorSearchService service = new DoctorSearchService(repository);
        Document match = new Document("doc-1", "Heart specialist", Map.of("specialty", "cardiology"));
        given(repository.semanticSearchByDoctors(org.mockito.ArgumentMatchers.any(SearchRequest.class)))
            .willReturn(List.of(match));

        List<DoctorSearchResult> results = service.searchDocuments("chest pain", 3, 0.75, "cardiology");

        ArgumentCaptor<SearchRequest> requestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
        then(repository).should().semanticSearchByDoctors(requestCaptor.capture());
        SearchRequest request = requestCaptor.getValue();
        assertThat(request.getQuery()).isEqualTo("chest pain");
        assertThat(request.getTopK()).isEqualTo(3);
        assertThat(request.getSimilarityThreshold()).isEqualTo(0.75);
        assertThat(request.hasFilterExpression()).isTrue();
        assertThat(results)
            .containsExactly(new DoctorSearchResult("doc-1", "Heart specialist", Map.of("specialty", "cardiology"), null));
    }
}
