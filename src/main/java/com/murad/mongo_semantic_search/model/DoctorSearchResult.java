package com.murad.mongo_semantic_search.model;

import java.util.Map;

public record DoctorSearchResult(
    String id,
    String content,
    Map<String, Object> metadata,
    Double score
) {
}
