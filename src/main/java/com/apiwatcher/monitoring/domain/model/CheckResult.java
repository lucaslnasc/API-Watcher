package com.apiwatcher.monitoring.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: Resultado de um health check.
 * Imutável - representa o resultado de uma verificação específica.
 */
public class CheckResult {

  private final String id;
  private final String apiId;
  private final boolean success;
  private final int statusCode;
  private final long latencyMs;
  private final String errorMessage;
  private final LocalDateTime checkedAt;

  // Construtor privado - use os factory methods
  private CheckResult(String apiId, boolean success, int statusCode, long latencyMs, String errorMessage) {
    this.id = UUID.randomUUID().toString();
    this.apiId = apiId;
    this.success = success;
    this.statusCode = statusCode;
    this.latencyMs = latencyMs;
    this.errorMessage = errorMessage;
    this.checkedAt = LocalDateTime.now();
  }

  // Factory methods
  public static CheckResult success(String apiId, int statusCode, long latencyMs) {
    return new CheckResult(apiId, true, statusCode, latencyMs, null);
  }

  public static CheckResult failure(String apiId, int statusCode, long latencyMs, String errorMessage) {
    return new CheckResult(apiId, false, statusCode, latencyMs, errorMessage);
  }

  public static CheckResult error(String apiId, String errorMessage) {
    return new CheckResult(apiId, false, 0, 0, errorMessage);
  }

  // Métodos de negócio
  public boolean exceededThreshold(int thresholdMs) {
    return latencyMs > thresholdMs;
  }

  public boolean isHealthy() {
    return success && statusCode >= 200 && statusCode < 300;
  }

  // Getters
  public String getId() {
    return id;
  }

  public String getApiId() {
    return apiId;
  }

  public boolean isSuccess() {
    return success;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public long getLatencyMs() {
    return latencyMs;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public LocalDateTime getCheckedAt() {
    return checkedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CheckResult that = (CheckResult) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "CheckResult{" +
        "apiId='" + apiId + '\'' +
        ", success=" + success +
        ", statusCode=" + statusCode +
        ", latencyMs=" + latencyMs +
        ", checkedAt=" + checkedAt +
        '}';
  }
}
