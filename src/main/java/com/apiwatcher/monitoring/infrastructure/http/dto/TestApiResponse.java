package com.apiwatcher.monitoring.infrastructure.http.dto;

/**
 * DTO com resultado do teste da API.
 */
public record TestApiResponse(
    boolean success,
    int statusCode,
    long latencyMs,
    String errorMessage,

    // Sugestões automáticas
    int suggestedThreshold,
    int suggestedExpectedStatusCode,
    String recommendation) {
  public static TestApiResponse success(int statusCode, long latencyMs) {
    // Calcula threshold sugerido: latência medida + 50% de margem
    int suggestedThreshold = (int) (latencyMs * 1.5);

    // Mínimo de 500ms, máximo de 30 segundos
    if (suggestedThreshold < 500)
      suggestedThreshold = 500;
    if (suggestedThreshold > 30000)
      suggestedThreshold = 30000;

    String recommendation = String.format(
        "API respondeu em %dms. Threshold sugerido: %dms (50%% de margem de segurança)",
        latencyMs, suggestedThreshold);

    return new TestApiResponse(
        true,
        statusCode,
        latencyMs,
        null,
        suggestedThreshold,
        statusCode, // Usa o status code retornado
        recommendation);
  }

  public static TestApiResponse failure(int statusCode, long latencyMs, String errorMessage) {
    return new TestApiResponse(
        false,
        statusCode,
        latencyMs,
        errorMessage,
        5000, // Threshold padrão em caso de falha
        200, // Espera 200 por padrão
        "API retornou erro. Verifique se a URL está correta.");
  }

  public static TestApiResponse error(String errorMessage) {
    return new TestApiResponse(
        false,
        0,
        0,
        errorMessage,
        5000,
        200,
        "Não foi possível conectar à API. Verifique a URL e tente novamente.");
  }
}
