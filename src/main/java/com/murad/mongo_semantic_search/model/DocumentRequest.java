package com.murad.mongo_semantic_search.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record DocumentRequest(
    @NotBlank @Size(max = 24_000) String content,
    @NotNull Map<String, Object> metadata
) {
}
