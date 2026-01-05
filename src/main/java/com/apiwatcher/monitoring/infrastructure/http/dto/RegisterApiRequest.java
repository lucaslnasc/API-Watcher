package com.apiwatcher.monitoring.infrastructure.http.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisição de cadastro de API.
 */
public record RegisterApiRequest(
    @NotBlank(message = "Nome é obrigatório") String name,

    @NotBlank(message = "URL é obrigatória") String url,

    @NotBlank(message = "Método HTTP é obrigatório") String httpMethod,

    @NotNull(message = "Status code esperado é obrigatório") @Min(value = 100, message = "Status code deve ser >= 100") @Max(value = 599, message = "Status code deve ser <= 599") Integer expectedStatusCode,

    @NotNull(message = "Threshold de latência é obrigatório") @Min(value = 0, message = "Threshold de latência deve ser >= 0") Integer latencyThresholdMs) {
}
