package com.apiwatcher.monitoring.infrastructure.http.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para teste inicial de API (descobre threshold automaticamente).
 */
public record TestApiRequest(
    @NotBlank(message = "Nome é obrigatório") String name,

    @NotBlank(message = "URL é obrigatória") String url,

    @NotBlank(message = "Método HTTP é obrigatório") String httpMethod) {
}
