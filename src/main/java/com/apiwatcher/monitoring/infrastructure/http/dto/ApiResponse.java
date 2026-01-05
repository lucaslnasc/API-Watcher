package com.apiwatcher.monitoring.infrastructure.http.dto;

import java.time.LocalDateTime;

import com.apiwatcher.monitoring.domain.model.MonitoredApi;

/**
 * DTO para resposta com dados da API monitorada.
 */
public record ApiResponse(
    String id,
    String name,
    String url,
    String httpMethod,
    Integer expectedStatusCode,
    Integer latencyThresholdMs,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public static ApiResponse from(MonitoredApi api) {
    return new ApiResponse(
        api.getId(),
        api.getName(),
        api.getUrl(),
        api.getHttpMethod(),
        api.getExpectedStatusCode(),
        api.getLatencyThresholdMs(),
        api.isActive(),
        api.getCreatedAt(),
        api.getUpdatedAt());
  }
}
